package gift.controller;

import gift.model.User;
import gift.model.UserRepository;
import gift.model.UserResponse;
import gift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/protected")
public class ProtectedController {

    private UserService userService;

    @Autowired
    public ProtectedController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> getProtectedResource(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (userService.validateToken(token)) {
            return ResponseEntity.ok("Protected resource accessed!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable("id") Long id) {
        String token = authHeader.replace("Bearer ", "");
        if (userService.validateToken(token)) {
            Long userIdFromToken = userService.getUserIdFromToken(token);
            if(userIdFromToken != null && userIdFromToken.equals(id)) {
                UserResponse userResponse = userService.getUserResponse(id);
                return ResponseEntity.ok(userResponse);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}