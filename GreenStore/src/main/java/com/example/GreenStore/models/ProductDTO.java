package com.example.GreenStore.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private double price;
    private String mood;
    private String texture;
    private int quantity;

}
