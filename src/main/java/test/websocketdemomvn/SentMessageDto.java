package test.websocketdemomvn;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Message}
 */
public record SentMessageDto(long receiverUserId, String content) implements Serializable {
}