package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter @Setter @ToString
public class OrderDto {

    @NotNull
    private UUID orderId;

    @NotBlank
    private String username;

    @NotNull
    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, @NotNull @PositiveOrZero Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    @NotNull
    private OrderState state;

    @PositiveOrZero
    private Double deliveryWeight = 0.0;

    @PositiveOrZero
    private Double deliveryVolume = 0.0;

    private boolean fragile;

    @PositiveOrZero
    private Double totalPrice = 0.0;

    @PositiveOrZero
    private Double deliveryPrice = 0.0;

    @PositiveOrZero
    private Double productPrice = 0.0;
}
