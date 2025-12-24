package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.*;
import com.example.GreenStore.repositories.StoreRepository;
import com.example.GreenStore.repositories.UserRepository;
import com.example.GreenStore.repositories.productRepository;
import com.example.GreenStore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    private final com.example.GreenStore.repositories.productRepository productRepository;

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

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductDTO>> getAllProducts(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Product> products2 = productRepository.findAll();
        List<Product> products3 = new ArrayList<>(List.of());
        for (Product product: products2) {
            if (!user.containProduct(product)) {
                products3.add(product);
            }
        }
        List<ProductDTO> products = products3.stream()
                .map(p -> new ProductDTO(
                        p.getId(),
                        p.getStoreId(),
                        p.getStoreName(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getMood(),
                        p.getTexture(),
                        p.getQuantity(),
                        p.isEcoFriendly(),
                        p.isPreorder()
                ))
                .toList();

        return ResponseEntity.ok(products);
    }


    @GetMapping("/getFilterProducts")
    public ResponseEntity<List<ProductDTO>> getFilterProducts(@RequestHeader("Authorization") String token,@RequestParam String search,@RequestParam String mood) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Product> products1 = productRepository.findAll();
        List<Product> products2 = new ArrayList<>(List.of());
        if (!mood.equals("all") && !mood.isEmpty()) {
            for (Product product : products1) {
                if (product.getMood().equals(mood)) {
                    products2.add(product);
                }
            }
        }else {
            products2.addAll(products1);
        }
        List<Product> products4 = new ArrayList<>(List.of());
        if (!search.isEmpty()) {
            for (Product product : products2) {
                if (product.getName().toLowerCase().trim().startsWith(search.toLowerCase().trim()) ||
                        product.getDescription().toLowerCase().trim().startsWith(search.toLowerCase().trim()) ||
                        product.getStoreName().toLowerCase().trim().startsWith(search.toLowerCase().trim())) {
                    products4.add(product);
                }
            }
        }
        else {
            products4.addAll(products2);
        }
        List<Product> products3 = new ArrayList<>(List.of());
        for (Product product: products4) {
            if (!user.containProduct(product)) {
                products3.add(product);
            }
        }
        List<ProductDTO> products = products3.stream()
                .map(p -> new ProductDTO(
                        p.getId(),
                        p.getStoreId(),
                        p.getStoreName(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getMood(),
                        p.getTexture(),
                        p.getQuantity(),
                        p.isEcoFriendly(),
                        p.isPreorder()
                ))
                .toList();

        return ResponseEntity.ok(products);
    }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader("Authorization") String token,@RequestBody List<CartItemDTO> cartItems) {
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isPresent()
                ? userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).get() : null;
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        if (user.getBalance() < totalPrice) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "موجودی کارت شما کافی نیست!");
            return ResponseEntity.badRequest().body(error);
        }

        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);
        int temp=0;

        for (CartItemDTO item : cartItems) {
            Product product = productRepository.findById(item.getProductid())
                    .orElseThrow(() -> new RuntimeException("محصول یافت نشد: " + item.getName()));

            if (product.getQuantity() < item.getQuantity()) {
                return ResponseEntity.badRequest().body("محصول تمام شده: " + product.getName());
            }
            if (product.isEcoFriendly() && temp==0) {
                temp=1;
                user.setBalance(user.getBalance() + (double) (totalPrice * 2) /100);
            }
            for (User user1 : userRepository.findAll()) {
                if (user1.getStoreById(item.getStoreid()) != null) {
                    user1.setBalance(user1.getBalance() + (item.getQuantity()*item.getPrice()));
                    break;
                }
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
            Store store = storeRepository.findById(item.getStoreid()).get();
            storeRepository.save(store);
        }

        StringBuilder history = new StringBuilder(user.getOrderHistory() != null ? user.getOrderHistory() : "");

        history.append("تاریخ: ").append(LocalDateTime.now())
                .append(" | خرید: ");

        for (CartItemDTO item : cartItems) {
            history.append(item.getName())
                    .append(" x").append(item.getQuantity())
                    .append(", ");
        }

        if (!cartItems.isEmpty()) {
            history.setLength(history.length() - 2);
        }

        history.append("\n");

        user.setOrderHistory(history);
        userRepository.save(user);
        if (temp == 1) {
            return new ResponseEntity<>("خرید با موفقیت انجام شد به خاطر خرید از محصولات سبز 0.02 درصد از مبلغ فاکتور شما به حساب شما برگردانده شد",HttpStatus.OK);
        }
        return ResponseEntity.ok("خرید با موفقیت انجام شد");
    }
    @GetMapping("/getOrderHistory")
    public ResponseEntity<String> getOrderHistory(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (user.getOrderHistory() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user.getOrderHistory().toString());
    }

}

    //edit product
    //remove product

    //store dashboard
    //search products by mood
    //shopping cart
    //buy product
    //history of orders


    //comment
    



