package com.example.GreenStore.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long storeId;

    private String storeName;

    @Column(unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    private double price;

    private String mood;

    private String texture;

    private int quantity;
    @JsonProperty("ecoFriendly")
    private boolean ecoFriendly;
    @JsonProperty("preorder")
    private boolean preorder;
}
