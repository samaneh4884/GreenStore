package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.*;
import com.example.GreenStore.repositories.StoreRepository;
import com.example.GreenStore.repositories.UserRepository;
import com.example.GreenStore.repositories.productRepository;
import com.example.GreenStore.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final productRepository productRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/SignUpStore")
    public ResponseEntity<String> SignUpStore(@RequestHeader("Authorization") String token, @RequestBody Store store) {
        if (storeRepository.findByName(store.getName()).isPresent()) {
            return new ResponseEntity<>("name is already in use", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isEmpty()) {
            return new ResponseEntity<>("user not found please as the first loggedIn", HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).get();
        if (!passwordEncoder.matches(store.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("your password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (store.getAddress() == null || store.getAddress().isEmpty() || store.getPhone().isEmpty() || store.getDescription().isEmpty() || store.getName().isEmpty()) {
            return new ResponseEntity<>("name or address or description or phone can not be empty", HttpStatus.BAD_REQUEST);
        }


        storeRepository.save(store);
        user.getUserStores().add(new UserStore(store, user));
        userRepository.save(user);
        return new ResponseEntity<>("Store created", HttpStatus.CREATED);
    }
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<String> deleteProduct(@RequestHeader("Authorization") String token, @RequestParam Long storeId, @RequestParam Long productId) {
        if (userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isEmpty()) {
            return new ResponseEntity<>("user not found please as the first loggedIn", HttpStatus.NOT_FOUND);
        }
        if (productRepository.findById(productId).isPresent() && storeRepository.findById(storeId).isPresent()) {
            Product product = productRepository.findById(productId).get();
            Store store = storeRepository.findById(storeId).get();
            store.deleteProductById(productId);
            productRepository.delete(product);
            storeRepository.save(store);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("product or store not found", HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/deleteStore")
    public ResponseEntity<String> deleteStore(@RequestHeader("Authorization") String token, @RequestParam Long storeId) {
        if (userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isEmpty()) {
            return new ResponseEntity<>("user not found please as the first loggedIn", HttpStatus.NOT_FOUND);
        }
        if (storeRepository.findById(storeId).isPresent()) {
            Store store = storeRepository.findById(storeId).get();
            for (StoreProduct storeProduct : store.getProducts()) {
                productRepository.delete(storeProduct.getProduct());
            }
            User user = userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).get();
            user.deleteStore(storeId);

            storeRepository.delete(store);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("product or store not found", HttpStatus.NOT_FOUND);
    }
    @PostMapping("/changeQuantity")
    public ResponseEntity<String> changeQuantity(@RequestHeader("Authorization") String token, @RequestParam Long storeId, @RequestParam Long productId, @RequestParam int quantity) {
        if (userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isEmpty()) {
            return new ResponseEntity<>("user not found please as the first loggedIn", HttpStatus.NOT_FOUND);
        }
        if (productRepository.findById(productId).isPresent() && storeRepository.findById(storeId).isPresent()) {
            Product product = productRepository.findById(productId).get();
            Store store = storeRepository.findById(storeId).get();
            product.setQuantity(product.getQuantity() + quantity);
            productRepository.save(product);
            storeRepository.save(store);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("product or store not found", HttpStatus.NOT_FOUND);
    }



    @PostMapping("/addProduct")
    public ResponseEntity<String> addOrUpdateProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("product") String productJson,
            @RequestParam("storeId") Long storeId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        if (userRepository.findByUsername(username).isEmpty()) {
            return new ResponseEntity<>("User not found. Please login first.", HttpStatus.BAD_REQUEST);
        }
        if (storeRepository.findById(storeId).isEmpty()) {
            return new ResponseEntity<>("Store not found. Please login first.", HttpStatus.BAD_REQUEST);
        }

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);
        product.setStoreName(storeRepository.findById(storeId).get().getName());
        product.setStoreId(storeRepository.findById(storeId).get().getId());

        Optional<Store> storeOpt = storeRepository.findById(storeId);
        if (storeOpt.isEmpty()) {
            return new ResponseEntity<>("Store not found.", HttpStatus.NOT_FOUND);
        }
        Store store = storeOpt.get();

        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            String webPath = path.toString().replace('\\', '/');
            product.setTexture(webPath);
        }

        productRepository.save(product);

        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setStore(store);
        storeProduct.setProduct(product);
        store.getProducts().add(storeProduct);

        storeRepository.save(store);

        return new ResponseEntity<>("Product added successfully.", HttpStatus.OK);
    }



    @GetMapping("/getInfo")
    public ResponseEntity<Store> getStoreInfo(@RequestHeader("Authorization") String token, @RequestParam long
            id) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).isPresent() ? userRepository.findByUsername(username).get() : null;
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Store store = storeRepository.findById(id).isPresent() ? storeRepository.findById(id).get() : null;
        if (store == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(store);
    }

    @GetMapping("/getStoreProducts")
    public ResponseEntity<List<ProductDTO>> getStoreProduct(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Store store = storeRepository.findById(id).orElse(null);
        if (store == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<ProductDTO> products = store.getProducts().stream()
                .map(sp -> sp.getProduct())
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
    @PostMapping("/sellStarter")
    public ResponseEntity<String> sellStarter(@RequestParam long storeId,@RequestParam long productId){
        Store store = storeRepository.findById(storeId).isPresent() ? storeRepository.findById(storeId).get() : null;
        if (store == null) return new ResponseEntity<>("Store not found.", HttpStatus.NOT_FOUND);
        Product product =  productRepository.findById(productId).isPresent() ? productRepository.findById(productId).get() : null;
        if (product == null) return new ResponseEntity<>("Product not found.", HttpStatus.NOT_FOUND);
        product.setPreorder(false);
        productRepository.save(product);
        storeRepository.save(store);
        return new ResponseEntity<>("Product sales started.", HttpStatus.OK);
    }






}
