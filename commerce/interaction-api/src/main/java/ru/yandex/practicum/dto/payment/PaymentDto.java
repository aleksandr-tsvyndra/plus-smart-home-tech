package ru.yandex.practicum.dto.payment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @ToString
public class PaymentDto {

    private UUID paymentId;

    @PositiveOrZero
    private BigDecimal totalPayment;

    @PositiveOrZero
    private BigDecimal deliveryTotal;

    @PositiveOrZero
    private BigDecimal feeTotal;

}
