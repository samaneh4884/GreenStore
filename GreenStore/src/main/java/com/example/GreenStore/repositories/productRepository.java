package com.example.GreenStore.repositories;

import com.example.GreenStore.models.Product;
import com.example.GreenStore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface productRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
}

