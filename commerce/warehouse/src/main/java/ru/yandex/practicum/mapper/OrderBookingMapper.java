package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.OrderBooking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderBookingMapper {

    BookedProductsDto toDto(OrderBooking entity);

}
