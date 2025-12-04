package ru.yandex.praktikum.scooter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    // Создание заказа: POST /api/v1/orders
    public Response create(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    // Получение списка заказов: GET /api/v1/orders
    public Response getOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }
}
