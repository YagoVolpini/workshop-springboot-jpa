package com.example.demo.services;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entities.Category;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.services.exceptions.AlreadyExistsException;
import com.example.demo.services.exceptions.DatabaseException;
import com.example.demo.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {


    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {

        return categoryRepository.findAll(pageable).map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        return new CategoryDTO(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO insert(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new AlreadyExistsException(String.format("Category with name %s already exists", dto.getName()));
        }

        Category category = new Category();
        updateData(dto, category);
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        updateData(dto, category);
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    private void updateData(CategoryDTO dto, Category category) {
        if (dto.getName() != null) category.setName(dto.getName());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        try {
            categoryRepository.delete(category);
            categoryRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this resource because it is associated with other records");
        }
    }
}
