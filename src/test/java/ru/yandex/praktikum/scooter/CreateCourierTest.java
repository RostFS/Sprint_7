package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courier;        // Курьер, которого реально создали (для удаления)
    private Integer courierId;      // Его id

    // 1. Курьера можно создать
    @Test
    @Description("Курьера можно создать — ожидаем 201 и ok=true")
    public void courierCanBeCreated() {
        courier = Courier.random();

        Response createResponse = createCourier(courier);

        assertCourierCreated(createResponse);
    }

    // 2. Нельзя создать двух одинаковых курьеров
    @Test
    @Description("Нельзя создать двух одинаковых курьеров — второй запрос возвращает 409 и текст ошибки")
    public void cannotCreateTwoIdenticalCouriers() {
        courier = Courier.random();

        // Первый запрос — успешное создание
        Response firstCreateResponse = createCourier(courier);
        assertCourierCreated(firstCreateResponse);

        // Второй запрос с теми же данными — уже ошибка
        Response secondCreateResponse = createCourier(courier);
        assertError(
                secondCreateResponse,
                409,
                "Этот логин уже используется. Попробуйте другой."
        );
    }

    // 3. Нельзя создать курьера без логина
    @Test
    @Description("Нельзя создать курьера без логина — ожидаем 400 и текст ошибки")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "password123", "NoLogin");

        Response response = createCourier(courierWithoutLogin);

        assertError(
                response,
                400,
                "Недостаточно данных для создания учетной записи"
        );
        // courier поле НЕ трогаем -> tearDown ничего не будет удалять
    }

    // 4. Нельзя создать курьера без пароля
    @Test
    @Description("Нельзя создать курьера без пароля — ожидаем 400 и текст ошибки")
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier("loginWithoutPassword", null, "NoPassword");

        Response response = createCourier(courierWithoutPassword);

        assertError(
                response,
                400,
                "Недостаточно данных для создания учетной записи"
        );
    }

    // 5. Если логин пустой, запрос возвращает ошибку
    @Test
    @Description("Нельзя создать курьера с пустым логином — ожидаем 400 и текст ошибки")
    public void cannotCreateCourierWithEmptyLogin() {
        Courier courierWithEmptyLogin = new Courier("", "password123", "EmptyLogin");

        Response response = createCourier(courierWithEmptyLogin);

        assertError(
                response,
                400,
                "Недостаточно данных для создания учетной записи"
        );
    }

    // 6. Если пароль пустой, запрос возвращает ошибку
    @Test
    @Description("Нельзя создать курьера с пустым паролем — ожидаем 400 и текст ошибки")
    public void cannotCreateCourierWithEmptyPassword() {
        Courier courierWithEmptyPassword = new Courier("loginEmptyPassword", "", "EmptyPassword");

        Response response = createCourier(courierWithEmptyPassword);

        assertError(
                response,
                400,
                "Недостаточно данных для создания учетной записи"
        );
    }

    @After
    public void tearDown() {
        deleteCreatedCourierIfNeeded();
    }

    // ---------- Allure steps ----------

    @Step("Создаём курьера")
    private Response createCourier(Courier courier) {
        return courierClient.create(courier);
    }

    @Step("Проверяем успешное создание курьера (201 и ok=true)")
    private void assertCourierCreated(Response response) {
        response.then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Step("Проверяем, что вернулась ошибка с кодом {status} и текстом \"{message}\"")
    private void assertError(Response response, int status, String message) {
        response.then()
                .statusCode(status)
                .body("message", equalTo(message));
    }

    @Step("Удаляем созданного курьера (если он был создан)")
    private void deleteCreatedCourierIfNeeded() {
        // Чистим только тех курьеров, которых действительно создали в тесте
        if (courier != null) {
            Response loginResponse = courierClient.login(CourierCredentials.from(courier));
            courierId = loginResponse.then().extract().path("id");

            if (courierId != null) {
                courierClient.delete(courierId);
            }

            // Сбрасываем состояние, чтобы не тянуться в другие тесты
            courier = null;
            courierId = null;
        }
    }
}
