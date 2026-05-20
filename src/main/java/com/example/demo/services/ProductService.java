package com.example.demo.services;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        Optional<Product> obj = productRepository.findById(id);
        return obj.get();
    }

    @Transactional
    public Product insert(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product entity = productRepository.getReferenceById(id);
        updateData(entity, product);
        return productRepository.save(entity);
    }

    private void updateData(Product entity, Product product) {
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setImgURL(product.getImgURL());
    }
}
