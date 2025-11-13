package com.example.GreenStore.Controllers;

import com.example.GreenStore.models.Product;
import com.example.GreenStore.models.Store;
import com.example.GreenStore.models.StoreProduct;
import com.example.GreenStore.repositories.StoreRepository;
import com.example.GreenStore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreRepository storeRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/AddOrUpdateProduct")
    public ResponseEntity<String> AddOrUpdateProduct(@RequestHeader("Authorization") String token
            , @RequestBody Product product) {
        String name = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Store store;
        if (storeRepository.findByName(name).isPresent()) {
            store = storeRepository.findByName(name).get();
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (store.getProductByName(product.getName()) != null) {
            store.getProductByName(product.getName()).setDescription(product.getDescription());
            store.getProductByName(product.getName()).setMood(product.getMood());
            store.getProductByName(product.getName()).setTexture(product.getTexture());
            store.getProductByName(product.getName()).setPrice(product.getPrice());
            store.getProductByName(product.getName()).setQuantity(product.getQuantity());
            store.getProductByName(product.getName()).setStoreName(product.getStoreName());
            return new ResponseEntity<>("Product updated", HttpStatus.OK);
        }
        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setStore(store);
        storeProduct.setProduct(product);
        store.getProducts().add(storeProduct);
        return new ResponseEntity<>("Product added", HttpStatus.OK);
    }

    @PostMapping("/increaseQuantity")
    public ResponseEntity<String> increaseQuantity(@RequestHeader("Authorization") String token
            , @RequestParam String productName, @RequestParam int quantity) {
        String name = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Store store;
        if (storeRepository.findByName(name).isPresent()) {
            store = storeRepository.findByName(name).get();
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        store.getProductByName(productName).setQuantity(store.getProductByName(productName).getQuantity() + quantity);
        return new ResponseEntity<>("Product inventory increased", HttpStatus.OK);
    }


}
