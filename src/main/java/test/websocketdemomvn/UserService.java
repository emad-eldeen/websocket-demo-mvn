package test.websocketdemomvn;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    public Optional<User> findUserById(long id) {
        // Implement logic to find and return a User by id
        return Optional.of(User.builder().build()); // Placeholder
    }

    public Optional<User> findUserByUsername(String username) {
        // Implement logic to find and return a User by username
        return Optional.of(User.builder().build()); // Placeholder
    }
}
