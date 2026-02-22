package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.model.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(
            target = "feeTotal",
            expression = "java(payment.getTotalPayment().subtract(payment.getTotalProduct()).subtract(payment.getDeliveryTotal()))"
    )
    PaymentDto toDto(Payment payment);

}
