package test.websocketdemomvn;

import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    public boolean validateToken(String token) {
        // Implement token validation logic here
        return true; // Placeholder
    }

    public String getUsername(String token) {
        // Implement logic to extract username from token here
        return "user"; // Placeholder
    }
}
