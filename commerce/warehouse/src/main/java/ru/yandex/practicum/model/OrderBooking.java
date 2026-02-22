package ru.yandex.practicum.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_bookings")
@Getter @Setter @ToString
public class OrderBooking {

    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @ElementCollection
    @CollectionTable(name = "booked_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    private Map<UUID, Integer> products;

    @Column(name = "delivery_volume")
    private Double deliveryVolume = 0.0;

    @Column(name = "delivery_weight")
    private Double deliveryWeight = 0.0;

    private Boolean fragile = false;

    @Column(name = "delivery_id")
    private UUID deliveryId;
}
