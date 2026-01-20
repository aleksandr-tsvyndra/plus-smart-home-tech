package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.yandex.practicum.dto.warehouse.DimensionDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper {

    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "width", qualifiedByName = "getWidthFromDimensionDto", source = "dimension")
    @Mapping(target = "height", qualifiedByName = "getHeightFromDimensionDto", source = "dimension")
    @Mapping(target = "depth", qualifiedByName = "getDepthFromDimensionDto", source = "dimension")
    WarehouseProduct toEntity(NewProductInWarehouseRequest request);

    @Named("getWidthFromDimensionDto")
    default Double getWidthFromDimensionDto(DimensionDto dimension) {
        return dimension.getWidth();
    }

    @Named("getHeightFromDimensionDto")
    default Double getHeightFromDimensionDto(DimensionDto dimension) {
        return dimension.getHeight();
    }

    @Named("getDepthFromDimensionDto")
    default Double getDepthFromDimensionDto(DimensionDto dimension) {
        return dimension.getDepth();
    }
}
