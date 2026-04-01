package ru.yandex.practicum.commerce.warehouse.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WarehouseErrorResponse(String userMessage, String message, String httpStatus) {
}
