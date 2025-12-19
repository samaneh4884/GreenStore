package com.example.GreenStore.repositories;

import com.example.GreenStore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface productRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
}

