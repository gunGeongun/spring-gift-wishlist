package gift.service;
import gift.model.User;
import gift.model.UserRepository;
import gift.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private Map<String, String> tokenStore = new ConcurrentHashMap<>();
    public boolean register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public Optional<String> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            User existingUser = user.get();
            existingUser.setToken(token);
            userRepository.updateUserToken(existingUser.getId(),token);
            return Optional.of(token);
        }
        return Optional.empty();
    }
    public UserResponse getUserResponse(Long id) {
        User user = userRepository.findById(id);
        return new UserResponse(user.getId(), user.getEmail());
    }

    public boolean validateToken(String token) {
        return userRepository.findByToken(token).isPresent();
    }

    public Long getUserIdFromToken(String token) {
        return userRepository.findByToken(token)
                .map(User::getId)
                .orElse(null);
    }
}