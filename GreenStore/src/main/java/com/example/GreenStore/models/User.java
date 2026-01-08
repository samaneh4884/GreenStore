package com.example.GreenStore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String email;

    private double balance = 0.0;

    private String address;

    private StringBuilder OrderHistory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserStore> userStores = new ArrayList<>();

    public Store getStoreById(Long id) {
        for (UserStore userStore : userStores) {
            if (userStore.getStore().getId().equals(id)) {
                return userStore.getStore();
            }
        }
        return null;
    }
    public void deleteStore(Long storeId) {
        for (UserStore userStore : userStores) {
            if (userStore.getStore().getId().equals(storeId)) {
                userStores.remove(userStore);
                break;
            }
        }
    }
    public boolean containProduct(Product product) {
        for (UserStore userStore : userStores) {
            if (product.getStoreId().equals(userStore.getStore().getId())) {
                return true;
            }
        }
        return false;
    }










}
