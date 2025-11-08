package test.websocketdemomvn;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageRestController {

    private final MessageRepository messageRepo;

    public MessageRestController(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @GetMapping("/{userId}")
    public List<Message> getMessages(@PathVariable String userId, Principal principal) {
        return null; // TODO
//        return messageRepo.findBySenderAndReceiver(principal.getName(), user);
    }
}
