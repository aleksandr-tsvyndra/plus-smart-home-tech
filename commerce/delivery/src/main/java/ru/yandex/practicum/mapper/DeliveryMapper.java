package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeliveryMapper {

    @Mapping(target = "addressId", ignore = true)
    Address toAddress(AddressDto dto);

    AddressDto toAddressDto(Address address);

    Delivery toEntity(DeliveryDto dto);

    DeliveryDto toDto(Delivery entity);

}
