package com.example.demo.services;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.projections.ProductMinProjection;
import com.example.demo.services.exceptions.AlreadyExistsException;
import com.example.demo.services.exceptions.BusinessException;
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
public class ProductService {


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        return new ProductDTO(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductDTO insert(ProductDTO dto) {

        if (productRepository.existsByName(dto.getName())) {
            throw new AlreadyExistsException(String.format("Product with name %s already exists", dto.getName()));
        }

        Product product = new Product();
        updateData(dto, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this resource because it is associated with other records");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        updateData(dto, product);
        product = productRepository.save(product);
        return new ProductDTO(product);

    }

    private void updateData(ProductDTO dto, Product entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getImgUrl() != null) entity.setImgUrl(dto.getImgUrl());
        if (dto.getStock() != null) entity.setStock(dto.getStock());

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            entity.getCategories().clear();
            for (CategoryDTO categoryDTO : dto.getCategories()) {
                Category category = categoryRepository.findById(categoryDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryDTO.getId()));
                entity.getCategories().add(category);

            }
        }

    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllByCategory(Long id, String name, Pageable pageable) {
        if (id == null && (name == null || name.isBlank())) {
            throw new BusinessException("Provide at least an id or a category name");
        }
        if (id != null) {
            Page<Product> page = productRepository.findAllByCategoriesId(id, pageable);
            if (page.isEmpty())
                throw new ResourceNotFoundException("Resource not found with id " + id);
            return page.map(ProductDTO::new);
        }

        Page<Product> products = productRepository.findAllByCategoriesNameContainingIgnoreCase(name, pageable);
        if (products.isEmpty()) {
            throw new BusinessException("No category found with name: " + name);
        }
        return products.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ProductMinProjection> searchByNameOrId(Long id, String name, Pageable pageable) {

        if (id == null && (name == null || name.isBlank())) {
            throw new BusinessException("Provide at least an id or a product name");
        }

        if (id != null) {
            Page<ProductMinProjection> page = productRepository.findByIdIs(id, pageable);
            if (page.isEmpty())
                throw new ResourceNotFoundException("Resource not found with id " + id);
            return page;
        }

        Page<ProductMinProjection> page = productRepository.findByNameContainingIgnoreCase(name, pageable);
        if (page.isEmpty())
            throw new BusinessException("No product found with name: " + name);
        return page;
    }
}
