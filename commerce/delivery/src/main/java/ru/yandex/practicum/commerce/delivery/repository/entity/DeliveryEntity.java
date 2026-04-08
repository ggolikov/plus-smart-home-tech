package ru.yandex.practicum.commerce.delivery.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class DeliveryEntity {

    @Id
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false, length = 32)
    private DeliveryState state;

    @Column(name = "estimated_cost", precision = 19, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "from_country", length = 255)
    private String fromCountry;

    @Column(name = "from_city", length = 255)
    private String fromCity;

    @Column(name = "from_street", length = 255)
    private String fromStreet;

    @Column(name = "from_house", length = 64)
    private String fromHouse;

    @Column(name = "from_flat", length = 64)
    private String fromFlat;

    @Column(name = "to_country", length = 255)
    private String toCountry;

    @Column(name = "to_city", length = 255)
    private String toCity;

    @Column(name = "to_street", length = 255)
    private String toStreet;

    @Column(name = "to_house", length = 64)
    private String toHouse;

    @Column(name = "to_flat", length = 64)
    private String toFlat;

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public DeliveryState getState() {
        return state;
    }

    public void setState(DeliveryState state) {
        this.state = state;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(String fromCountry) {
        this.fromCountry = fromCountry;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getFromStreet() {
        return fromStreet;
    }

    public void setFromStreet(String fromStreet) {
        this.fromStreet = fromStreet;
    }

    public String getFromHouse() {
        return fromHouse;
    }

    public void setFromHouse(String fromHouse) {
        this.fromHouse = fromHouse;
    }

    public String getFromFlat() {
        return fromFlat;
    }

    public void setFromFlat(String fromFlat) {
        this.fromFlat = fromFlat;
    }

    public String getToCountry() {
        return toCountry;
    }

    public void setToCountry(String toCountry) {
        this.toCountry = toCountry;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getToStreet() {
        return toStreet;
    }

    public void setToStreet(String toStreet) {
        this.toStreet = toStreet;
    }

    public String getToHouse() {
        return toHouse;
    }

    public void setToHouse(String toHouse) {
        this.toHouse = toHouse;
    }

    public String getToFlat() {
        return toFlat;
    }

    public void setToFlat(String toFlat) {
        this.toFlat = toFlat;
    }
}
