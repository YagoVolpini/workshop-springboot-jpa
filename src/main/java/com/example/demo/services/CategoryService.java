package com.example.demo.services;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entities.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.exceptions.AlreadyExistsException;
import com.example.demo.services.exceptions.DatabaseException;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(CategoryDTO::new).toList();
    }

    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return new CategoryDTO(category);
    }

    public CategoryDTO insert(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new AlreadyExistsException(String.format("Category with name %s already exists", dto.getName()));
        }


        Category category = new Category();
        updateData(dto, category);
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        updateData(dto, category);
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    public void updateData(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this resource because it is associated with other records");
        }
    }
}
