package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.Store;
import com.example.GreenStore.models.User;
import com.example.GreenStore.repositories.StoreRepository;
import com.example.GreenStore.repositories.UserRepository;
import com.example.GreenStore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;


    @PostMapping("/SignUpUser")
    public ResponseEntity<String> SignUpUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already in use", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    @GetMapping("/LoginUser")
    public ResponseEntity<String> LoginUser(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            return new ResponseEntity<>("Wrong password", HttpStatus.BAD_REQUEST);
        }

        String token = jwtUtil.generateToken(username, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/SignUpStore")
    public ResponseEntity<String> SignUpStore(@RequestBody Store store) {
        if (storeRepository.findByName(store.getName()).isPresent()) {
            return new ResponseEntity<>("name is already in use", HttpStatus.BAD_REQUEST);
        }
        storeRepository.save(store);
        return new ResponseEntity<>("Store created", HttpStatus.CREATED);
    }

    @GetMapping("/LoginStore")
    public ResponseEntity<String> LoginStore(@RequestParam String name, @RequestParam String password) {
        Store store = storeRepository.findByName(name).orElse(null);
        if (store == null) {
            return new ResponseEntity<>("store not found", HttpStatus.NOT_FOUND);
        }
        if (!store.getPassword().equals(password)) {
            return new ResponseEntity<>("Wrong password", HttpStatus.BAD_REQUEST);
        }

        String token = jwtUtil.generateToken(name, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }



}
