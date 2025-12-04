package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final OrderClient orderClient = new OrderClient();
    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    // Набор параметров:
    // 1) только BLACK
    // 2) только GREY
    // 3) BLACK + GREY
    // 4) без цвета
    @Parameterized.Parameters(name = "colors = {0}")
    public static Object[][] getColors() {
        return new Object[][]{
                {Collections.singletonList("BLACK")},
                {Collections.singletonList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {Collections.emptyList()}
        };
    }

    @Test
    @Description("Создание заказа с разными вариантами цвета самоката")
    public void orderCanBeCreatedWithDifferentColors() {
        Order order = Order.defaultWithColors(colors);

        Response response = createOrder(order);

        assertOrderCreated(response);
    }

    @Step("Создаём заказ с цветами: {order.colors}")
    private Response createOrder(Order order) {
        return orderClient.create(order);
    }

    @Step("Проверяем, что заказ успешно создан (201) и в ответе есть track")
    private void assertOrderCreated(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
