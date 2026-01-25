package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductState;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter @Setter @ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false)
    private Double price;
}
