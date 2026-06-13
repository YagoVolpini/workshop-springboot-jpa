package com.example.demo.dto;

import com.example.demo.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Price must be positive")
    @NotNull(message = "Price is required")
    private BigDecimal price;
    private String imgUrl;
    private List<CategoryDTO> categories = new ArrayList<>();

    @NotNull(message = "Stock is required")
    @Positive(message = "Stock must be positive")
    private Integer stock;


    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imgUrl = product.getImgUrl();
        this.stock = product.getStock();

        product.getCategories().forEach(category -> categories.add(new CategoryDTO(category)));
    }
}
