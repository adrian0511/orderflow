package com.adrian.productservice.service.impl;

import com.adrian.productservice.dto.request.ProductRequest;
import com.adrian.productservice.dto.response.ProductResponse;
import com.adrian.productservice.entity.Product;
import com.adrian.productservice.exception.ProductNotFoundException;
import com.adrian.productservice.mapper.ProductMapper;
import com.adrian.productservice.repository.ProductRepository;
import com.adrian.productservice.service.interf.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;


    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();

        return mapper.toResponse(repository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional
    public ProductResponse update(String id, ProductRequest request) {
        return mapper.toResponse(repository.findById(id)
                .map(p -> {
                    p.setName(request.getName());
                    p.setCategory(request.getCategory());
                    p.setPrice(request.getPrice());
                    p.setDescription(request.getDescription());

                    return repository.save(p);
                })
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id))
        );
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id))
            throw new ProductNotFoundException("Product not found with id: " + id);

        repository.deleteById(id);
    }
}
