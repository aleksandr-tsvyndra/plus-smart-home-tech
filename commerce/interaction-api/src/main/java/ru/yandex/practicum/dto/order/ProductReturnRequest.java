package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter @Setter @ToString
public class ProductReturnRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private Map<UUID, @NotNull @PositiveOrZero Integer> products;
}
