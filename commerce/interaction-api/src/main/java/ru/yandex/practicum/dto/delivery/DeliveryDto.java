package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull
    private UUID orderId;

    @NotNull
    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    private DeliveryState state = DeliveryState.CREATED;
}
