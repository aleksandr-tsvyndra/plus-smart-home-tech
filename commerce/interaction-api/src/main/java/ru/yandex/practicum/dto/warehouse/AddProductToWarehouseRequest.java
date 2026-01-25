package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
