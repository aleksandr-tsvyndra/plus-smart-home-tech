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
import ru.yandex.practicum.dto.shoppingCart.CartState;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Getter @Setter @ToString
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id", updatable = false, nullable = false)
    private UUID shoppingCartId;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "cart_state", nullable = false)
    private CartState cartState = CartState.ACTIVE;

    @ElementCollection
    @CollectionTable(name = "cart_products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;
}
