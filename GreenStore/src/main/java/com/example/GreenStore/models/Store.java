package com.example.GreenStore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    private String password;

    private String address;

    private String phone;

    private String description;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<StoreProduct> products = new ArrayList<>();

    public Product getProductByName(String name) {
        for (StoreProduct product : products) {
            if (product.getProduct().getName().equals(name)) {
                return product.getProduct();
            }
        }
        return null;
    }
    public void deleteProductById(Long id) {
        for (StoreProduct product : products) {
            if (product.getProduct().getId().equals(id)) {
                products.remove(product);
                break;
            }
        }
    }


}
