package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {
    private double deliveryWeight;

    private double deliveryVolume;

    private boolean fragile;
}
