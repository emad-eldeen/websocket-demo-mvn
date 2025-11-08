package test.websocketdemomvn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenService jwtTokenService;

    public WebSocketAuthInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {

        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        String fullUri = request.getURI().toString();
        
        log.info("WebSocket handshake request: path={}, query={}, fullUri={}", path, query, fullUri);
        log.info("Request headers: {}", request.getHeaders());
        
        // Allow SockJS info requests to pass through (they're just transport negotiation)
        // SockJS info endpoint is typically /ws/info
        // Note: HandshakeInterceptor is only called for WebSocket handshakes, not HTTP info requests
        if (path.endsWith("/info")) {
            log.info("Allowing SockJS info request for path: {}", path);
            // Extract and store token if present, for potential later use
            String token = extractTokenFromQuery(query);
            if (token != null) {
                attributes.put("token", token);
                log.info("Stored token from info request");
            }
            return true;
        }

        // Extract token from query parameters or headers
        String token = extractTokenFromQuery(query);
        if (token == null) {
            token = extractTokenFromHeaders(request);
        }
        // Also try to get token from attributes (stored during info request)
        if (token == null && attributes.containsKey("token")) {
            token = (String) attributes.get("token");
            log.info("Retrieved token from attributes");
        }

        if (token != null && jwtTokenService.validateToken(token)) {
            String username = jwtTokenService.getUsername(token);
            // Store username in attributes - Spring STOMP will use this to create Principal
            attributes.put("user", username);
            // Also store as Principal for direct access
            attributes.put("SPRING_SECURITY_CONTEXT_USERNAME", username);
            // Create a simple Principal for STOMP
            Principal principal = () -> username;
            attributes.put("PRINCIPAL", principal);
            log.info("WebSocket authentication successful for user: {}", username);
            return true;
        }
        
        log.warn("WebSocket authentication failed: no valid token found in path={}, query={}, fullUri={}", 
                path, query, fullUri);
        // If no token provided, reject the connection
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractTokenFromQuery(String query) {
        if (query == null) return null;
        try {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    String token = param.substring(6);
                    // Handle URL-encoded tokens
                    return java.net.URLDecoder.decode(token, "UTF-8");
                }
            }
        } catch (java.io.UnsupportedEncodingException e) {
            // UTF-8 is always supported, but handle exception anyway
            return null;
        }
        return null;
    }

    private String extractTokenFromHeaders(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }
}

