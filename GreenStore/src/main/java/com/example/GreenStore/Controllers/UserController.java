package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.Product;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
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
    public ResponseEntity<String> addBalance(@RequestHeader("Authorization") String token, @RequestParam Double balance) {
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

}
