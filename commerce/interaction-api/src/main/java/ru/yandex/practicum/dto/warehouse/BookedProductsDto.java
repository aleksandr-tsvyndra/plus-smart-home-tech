package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {
    @Positive
    @NotNull
    private Double deliveryWeight;

    @Positive
    @NotNull
    private Double deliveryVolume;

    @NotNull
    private Boolean fragile;
}
