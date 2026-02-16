package ru.yandex.practicum.dto.payment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
public class PaymentDto {

    private UUID paymentId;

    @PositiveOrZero
    private Double totalPayment;

    @PositiveOrZero
    private Double deliveryTotal;

    @PositiveOrZero
    private Double feeTotal;

}
