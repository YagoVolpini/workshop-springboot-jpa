package com.example.demo.dto;

import com.example.demo.entities.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;


    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
