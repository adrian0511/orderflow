package com.adrian.productservice.mapper;

import com.adrian.productservice.dto.response.ProductResponse;
import com.adrian.productservice.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

}
