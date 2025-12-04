package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersListTest extends BaseTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    @Description("Проверяем, что ручка получения списка заказов возвращает 200 и непустое поле orders")
    public void ordersListShouldBeReturned() {
        Response response = getOrdersList();

        assertOrdersListResponse(response);
    }

    // ---------- Allure steps ----------

    @Step("Отправляем запрос на получение списка заказов")
    private Response getOrdersList() {
        return orderClient.getOrders();
    }

    @Step("Проверяем, что статус 200 и поле 'orders' не null")
    private void assertOrdersListResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
