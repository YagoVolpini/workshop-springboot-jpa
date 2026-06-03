package com.example.demo.controllers;

import com.example.demo.dto.ProductDTO;
import com.example.demo.repositories.projections.ProductMinProjection;
import com.example.demo.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }


    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO product) {
        ProductDTO obj = productService.insert(product);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductDTO>> findAllByCategory(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.findAllByCategory(id ,name, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductMinProjection>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.searchByNameOrId(id, name, pageable));
    }

}
