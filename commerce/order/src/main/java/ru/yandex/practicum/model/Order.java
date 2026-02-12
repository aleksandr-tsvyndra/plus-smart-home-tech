package ru.yandex.practicum.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.dto.order.OrderState;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter @ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.NEW;

    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = " payment_id")
    private UUID paymentId;

    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    private Boolean fragile;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "product_price")
    private Double productPrice;

    @Column(name = "delivery_price")
    private Double deliveryPrice;

    @Column(nullable = false)
    private String username;
}
