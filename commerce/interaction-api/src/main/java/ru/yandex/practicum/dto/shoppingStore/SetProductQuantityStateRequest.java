package ru.yandex.practicum.dto.shoppingStore;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter @ToString
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}
