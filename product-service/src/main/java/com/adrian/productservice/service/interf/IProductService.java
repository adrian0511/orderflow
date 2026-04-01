package com.adrian.productservice.service.interf;

import com.adrian.productservice.dto.request.ProductRequest;
import com.adrian.productservice.dto.response.ProductResponse;

import java.util.List;

public interface IProductService {

    ProductResponse create(ProductRequest request);

    List<ProductResponse> getAll();

    ProductResponse getById(String id);

    ProductResponse update(String id, ProductRequest request);

    void delete(String id);
}
