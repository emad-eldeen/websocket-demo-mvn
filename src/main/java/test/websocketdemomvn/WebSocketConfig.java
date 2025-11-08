package test.websocketdemomvn;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String QUEUE_BROKER_DESTINATION_PREFIX = "/queue";

    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker(QUEUE_BROKER_DESTINATION_PREFIX);
        config.setUserDestinationPrefix("/user"); // for private chats
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            // TODO check if needed
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Get username from WebSocket session attributes
                    String username = (String) accessor.getSessionAttributes().get("user");
                    if (username != null) {
                        Principal principal = () -> username;
                        accessor.setUser(principal);
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // TODO check if needed
        // Plain WebSocket endpoint for native clients (Node.js, Postman, etc.)
        registry.addEndpoint("/ws")
                .addInterceptors(authInterceptor)
                .setAllowedOriginPatterns("*");
        
        // SockJS endpoint for browser clients (fallback support)
        registry.addEndpoint("/ws-sockjs")
                .addInterceptors(authInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
