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


//    @PostMapping("/editProduct")
//    public ResponseEntity<String> editProduct(@RequestHeader("Authorization") String token
//            , @RequestBody Product product) {
//        String name = jwtUtil.extractUsername(token.replace("Bearer ", ""));
//        Store store;
//        if (storeRepository.findByName(name).isPresent()) {
//            store = storeRepository.findByName(name).get();
//        } else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//        if (store.getProductByName(product.getName()) != null) {
//            store.getProductByName(product.getName()).setDescription(product.getDescription());
//            store.getProductByName(product.getName()).setMood(product.getMood());
//            store.getProductByName(product.getName()).setTexture(product.getTexture());
//            store.getProductByName(product.getName()).setPrice(product.getPrice());
//            store.getProductByName(product.getName()).setQuantity(product.getQuantity());
//            store.getProductByName(product.getName()).setStoreName(product.getStoreName());
//            return new ResponseEntity<>("Product updated", HttpStatus.OK);
//        }
//        StoreProduct storeProduct = new StoreProduct();
//        storeProduct.setStore(store);
//        storeProduct.setProduct(product);
//        store.getProducts().add(storeProduct);
//        return new ResponseEntity<>("Product added", HttpStatus.OK);
//    }

//    @PostMapping("/addProduct")
//    public ResponseEntity<String> AddOrUpdateProduct(@RequestHeader("Authorization") String token
//            , @RequestBody Product product,@RequestParam Long storeId,@RequestParam("file") MultipartFile file) throws IOException {
//        if (userRepository.findByUsername(jwtUtil.extractUsername(token.replace("Bearer ", ""))).isEmpty()) {
//            return new ResponseEntity<>("user not found please as the first loggedIn", HttpStatus.BAD_REQUEST);
//        }
//        Store store;
//        if (storeRepository.findById(storeId).isPresent()) {
//            store = storeRepository.findById(storeId).get();
//        } else
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//

    /// /        if (store.getProductByName(product.getName()) != null) {
    /// /            store.getProductByName(product.getName()).setDescription(product.getDescription());
    /// /            store.getProductByName(product.getName()).setMood(product.getMood());
    /// /            store.getProductByName(product.getName()).setTexture(product.getTexture());
    /// /            store.getProductByName(product.getName()).setPrice(product.getPrice());
    /// /            store.getProductByName(product.getName()).setQuantity(product.getQuantity());
    /// /            store.getProductByName(product.getName()).setStoreName(product.getStoreName());
    /// /            return new ResponseEntity<>("Product updated", HttpStatus.OK);
    /// /        }
//        String uploadDir = "uploads/";
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Path path = Paths.get(uploadDir + fileName);
//        Files.createDirectories(path.getParent());
//        Files.write(path, file.getBytes());
//        product.setTexture(path.toString());
//        productRepository.save(product);
//        StoreProduct storeProduct = new StoreProduct();
//        storeProduct.setStore(store);
//        storeProduct.setProduct(product);
//        store.getProducts().add(storeProduct);
//        storeRepository.save(store);
//        return new ResponseEntity<>("Product added", HttpStatus.OK);
//    }
    @PostMapping("/addProduct")
    public ResponseEntity<String> addOrUpdateProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("product") String productJson, // JSON محصول به صورت رشته
            @RequestParam("storeId") Long storeId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        if (userRepository.findByUsername(username).isEmpty()) {
            return new ResponseEntity<>("User not found. Please login first.", HttpStatus.BAD_REQUEST);
        }

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

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
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getMood(),
                        p.getTexture(),
                        p.getQuantity()
                ))
                .toList();

        return ResponseEntity.ok(products);
    }


//    @PostMapping("/decreaseQuantity")
//    public ResponseEntity<String> decreaseQuantity(@RequestHeader("Authorization") String token,
//                                                   @RequestParam Long productId) {
//        Product product = productRepository.findById(productId).orElseThrow();
//        if (product.getQuantity() > 0) product.setQuantity(product.getQuantity() - 1);
//        productRepository.save(product);
//        return ResponseEntity.ok("Quantity decreased");
//    }
//
//    @DeleteMapping("/removeProduct")
//    public ResponseEntity<String> removeProduct(@RequestHeader("Authorization") String token,
//                                                @RequestParam Long productId) {
//        Product product = productRepository.findById(productId).orElseThrow();
//        productRepository.delete(product);
//        return ResponseEntity.ok("Product removed");
//    }


}
