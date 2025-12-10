package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.Product;
import com.example.GreenStore.models.Store;
import com.example.GreenStore.models.User;
import com.example.GreenStore.models.UserStore;
import com.example.GreenStore.repositories.StoreRepository;
import com.example.GreenStore.repositories.UserRepository;
import com.example.GreenStore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/buy")
    public ResponseEntity<String> buy(@RequestHeader("Authorization") String token, @RequestParam String storeName
            , @RequestParam String productName, @RequestParam int quantity) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Store store = storeRepository.findByName(storeName).orElse(null);
        if (store == null) {
            return new ResponseEntity<>("Store not found", HttpStatus.NOT_FOUND);
        }
        Product p = store.getProductByName(productName);
        if (p == null) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
        if (user.getBalance() < quantity * p.getPrice()) {
            return new ResponseEntity<>("Not enough money", HttpStatus.BAD_REQUEST);
        }
        user.setBalance(user.getBalance() - quantity * p.getPrice());
        p.setQuantity(p.getQuantity() - quantity);
        user.getOrderHistory().append(p.getName()).append(" _ price: ").append(p.getPrice()).append(" _ quantity: ").append(quantity).append(" _from: ").append(storeName).append("\n");
        userRepository.save(user);
        return new ResponseEntity<>("your purchase was successful and the product was shipped to your address: "
                + user.getAddress(), HttpStatus.OK);

    }

    @PostMapping("/addBalance")
    public ResponseEntity<String> addBalance(@RequestHeader("Authorization") String token, @RequestParam double balance) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (balance <= 0) return new ResponseEntity<>("amount of money is negative", HttpStatus.BAD_REQUEST);
        user.setBalance(user.getBalance() + balance);
        userRepository.save(user);
        return new ResponseEntity<>("your balance is now " + user.getBalance(), HttpStatus.OK);
    }
    @GetMapping("/getBalance")
    public ResponseEntity<Double> getBalance(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.getBalance(), HttpStatus.OK);
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<User> editUserInfo(@RequestHeader ("Authorization") String token) {
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @PostMapping("/changeEmail")
    public ResponseEntity<String> changeEmail(@RequestHeader("Authorization") String token, @RequestParam String email) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        user.setEmail(email);
        userRepository.save(user);
        return new ResponseEntity<>("your email is now " + user.getEmail(), HttpStatus.OK);
    }
    @PostMapping("/changeAddress")
    public ResponseEntity<String> changeAddress(@RequestHeader("Authorization") String token, @RequestParam String address) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        user.setAddress(address);
        userRepository.save(user);
        return new ResponseEntity<>("your address is now " + user.getAddress(), HttpStatus.OK);
    }
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body
    ) {
        try {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");

            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            User user = userRepository.findByUsername(username).orElse(null);

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return new ResponseEntity<>("Old password is incorrect", HttpStatus.UNAUTHORIZED);
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            String newToken = jwtUtil.generateToken(user.getUsername(),user.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            response.put("token", newToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return new ResponseEntity<>("Error changing password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getUserStores")
    public ResponseEntity<List<Store>> getUserStores(@RequestHeader("Authorization") String token) {
        List<Store> stores = new ArrayList<>();
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (UserStore userStore: user.getUserStores()) {
            stores.add(userStore.getStore());
        }
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }


    //change logic of user and store
    //search products by mood
    //shopping cart
    //store dashboard
    //add product
    //edit product
    //remove product
    //buy product
    


}
