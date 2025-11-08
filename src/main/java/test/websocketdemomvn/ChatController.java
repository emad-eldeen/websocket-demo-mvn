package test.websocketdemomvn;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepo;
    private final UserService userService;
    private static final String MESSAGES_DESTINATION_PREFIX = "/messages";
    private static final String PRIVATE_MESSAGE_DESTINATION = WebSocketConfig.QUEUE_BROKER_DESTINATION_PREFIX + MESSAGES_DESTINATION_PREFIX;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SentMessageDto message, Principal principal) {

        // TODO
//        var sender = userService.findUserByUsername(principal.getName()).orElseThrow(
//                () -> new RuntimeException("User not found")
//        );
//        var receiver = userService.findUserById(message.receiverUserId()).orElseThrow(
//                () -> new RuntimeException("Receiver not found")
//        );
        var sender = User.builder().id(11L).build();
        var receiver = User.builder().id(11L).username("user").build();

        // save message
        Message saved = messageRepo.save(
                Message.builder()
                        .content(message.content())
                        .receiverUserId(message.receiverUserId())
                        .senderUserId(sender.getId())
                        .build()
        );
        // send to receiver
        messagingTemplate.convertAndSendToUser(receiver.getUsername(), PRIVATE_MESSAGE_DESTINATION, saved);
    }
}
