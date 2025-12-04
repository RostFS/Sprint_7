package ru.yandex.praktikum.scooter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    // Создание курьера: POST /api/v1/courier
    public Response create(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    // Логин курьера: POST /api/v1/courier/login
    public Response login(CourierCredentials credentials) {
        return given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }

    // Удаление курьера: DELETE /api/v1/courier/{id}
    public Response delete(int courierId) {
        return given()
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}
