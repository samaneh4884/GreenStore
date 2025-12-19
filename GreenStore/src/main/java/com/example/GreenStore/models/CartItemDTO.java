package com.example.GreenStore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long storeid;
    private Long productid;
    private String storeName;
    private String name;
    private int price;
    private int quantity;
}
