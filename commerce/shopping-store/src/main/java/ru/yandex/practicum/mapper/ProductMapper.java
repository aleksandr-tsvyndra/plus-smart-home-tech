package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product mapToProduct(ProductDto dto);

    ProductDto mapToProductDto(Product product);

    List<ProductDto> mapToProductDto(List<Product> products);

}
