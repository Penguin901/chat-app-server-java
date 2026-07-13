package com.example.chatapp;

import com.example.chatapp.chat.room.ChatRoomUseCase;
import com.example.chatapp.friend.FriendUseCase;
import com.example.chatapp.chat.message.ChatMessage;
import com.example.chatapp.chat.message.ChatMessageController;
import com.example.chatapp.chat.message.ChatMessageRepository;
import com.example.chatapp.chat.message.dto.response.SendMessageResponse;
import com.example.chatapp.chat.message.dto.request.SendMessageRequest;
import com.example.chatapp.chat.room.dto.request.CreateChatRoomRequest;
import com.example.chatapp.chat.room.dto.response.CreateChatRoomResponse;
import com.example.chatapp.security.UserPrincipal;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SendMessageIntegrationTest {

    private final UserRepository userRepository;
    private final FriendUseCase friendUseCase;
    private final ChatRoomUseCase chatRoomUseCase;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageController chatMessageController;

    @MockBean
    SimpMessagingTemplate simpMessagingTemplate;

    SendMessageIntegrationTest(UserRepository userRepository, FriendUseCase friendUseCase, ChatRoomUseCase chatRoomUseCase, ChatMessageRepository chatMessageRepository, ChatMessageController chatMessageController) {
        this.userRepository = userRepository;
        this.friendUseCase = friendUseCase;
        this.chatRoomUseCase = chatRoomUseCase;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageController = chatMessageController;
    }

    @Test
    void saveAndBroadcastMessage() {
        User sender = User.create("sender@test.com", "sender-oauth-id", "GOOGLE");
        userRepository.save(sender);

        User receiver = User.create("receiver@test.com", "receiver-oauth-id", "GOOGLE");
        userRepository.save(receiver);

        friendUseCase.addFriend(sender.getId(), receiver.getId());
        List<Long> participantIds = List.of(receiver.getId());

        CreateChatRoomRequest createChatRoomRequest = new CreateChatRoomRequest("", participantIds);
        CreateChatRoomResponse createChatRoomResponse = chatRoomUseCase.getOrCreateChatRoom(sender.getId(), createChatRoomRequest);

        SendMessageRequest sendMessageRequest = new SendMessageRequest(createChatRoomResponse.id(), "test 메세지");

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER_EDIT"));
        UserPrincipal userPrincipal = new UserPrincipal(sender.getId(), authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.authorities());

        chatMessageController.sendMessage(authentication, sendMessageRequest);
        List<ChatMessage> messages = chatMessageRepository.findAll();

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getMessageContent()).isEqualTo("test 메세지");

        ArgumentCaptor<SendMessageResponse> chatMessageEventArgumentCaptor = ArgumentCaptor.forClass(SendMessageResponse.class);

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/chatroom/" + createChatRoomResponse.id()),
                chatMessageEventArgumentCaptor.capture()
        );

        SendMessageResponse sendMessageResponseCaptorValue = chatMessageEventArgumentCaptor.getValue();

        assertThat(sendMessageResponseCaptorValue.sender().userId()).isEqualTo(sender.getId());
        assertThat(sendMessageResponseCaptorValue.chatRoomId()).isEqualTo(createChatRoomResponse.id());
        assertThat(sendMessageResponseCaptorValue.messageContent()).isEqualTo(messages.get(0).getMessageContent());
    }
}



