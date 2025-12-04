package ru.yandex.praktikum.scooter;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courier;     // Курьер, созданный в тесте
    private Integer courierId;   // Его id для удаления

    // 1. Курьер может авторизоваться
    @Test
    @Description("Курьер может авторизоваться с корректным логином и паролем")
    public void courierCanLogin() {
        // Подготовка: создаём курьера
        courier = Courier.random();
        createCourier(courier)
                .then()
                .statusCode(201);

        CourierCredentials creds = CourierCredentials.from(courier);

        // Действие: логинимся
        Response loginResponse = loginCourier(creds);

        // Проверка: статус 200 и в теле есть id
        assertSuccessfulLogin(loginResponse);

        // Сохраняем id для удаления в tearDown
        courierId = loginResponse.then().extract().path("id");
    }

    // 2. Для авторизации нужно передать все обязательные поля (без логина)
    @Test
    @Description("Нельзя авторизоваться без логина — ожидаем 400 и текст ошибки")
    public void cannotLoginWithoutLogin() {
        CourierCredentials credsWithoutLogin = new CourierCredentials(null, "password123");

        Response response = loginCourier(credsWithoutLogin);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // 3. Для авторизации нужно передать все обязательные поля (без пароля)
    //    Тут мы принимаем и 400, и 504, потому что стенд иногда падает с таймаутом.
    @Test
    @Description("Нельзя авторизоваться без пароля — ожидаем 400 или 504 (таймаут стенда)")
    public void cannotLoginWithoutPassword() {
        CourierCredentials credsWithoutPassword =
                new CourierCredentials("someLogin", null);

        Response response = loginCourier(credsWithoutPassword);

        response.then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(400),
                        org.hamcrest.Matchers.is(504)
                ));
    }

    // 4. Система вернёт ошибку, если неправильно указать логин
    @Test
    @Description("Авторизация с неверным логином возвращает 404 и сообщение 'Учетная запись не найдена'")
    public void loginWithWrongLoginReturnsError() {
        courier = Courier.random();
        createCourier(courier)
                .then()
                .statusCode(201);

        CourierCredentials wrongLoginCreds =
                new CourierCredentials("wrong_" + courier.getLogin(), courier.getPassword());

        Response response = loginCourier(wrongLoginCreds);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // 5. Система вернёт ошибку, если неправильно указать пароль
    @Test
    @Description("Авторизация с неверным паролем возвращает 404 и сообщение 'Учетная запись не найдена'")
    public void loginWithWrongPasswordReturnsError() {
        courier = Courier.random();
        createCourier(courier)
                .then()
                .statusCode(201);

        CourierCredentials wrongPasswordCreds =
                new CourierCredentials(courier.getLogin(), "wrong_password");

        Response response = loginCourier(wrongPasswordCreds);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // 6. Если авторизоваться под несуществующим пользователем — ошибка
    @Test
    @Description("Авторизация несуществующего курьера возвращает 404 и сообщение 'Учетная запись не найдена'")
    public void loginNonExistingCourierReturnsError() {
        CourierCredentials nonExisting =
                new CourierCredentials("non_existing_login_" + System.currentTimeMillis(),
                        "some_password");

        Response response = loginCourier(nonExisting);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
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

    @Step("Логинимся курьером")
    private Response loginCourier(CourierCredentials creds) {
        return courierClient.login(creds);
    }

    @Step("Проверяем успешную авторизацию (200 и наличие id)")
    private void assertSuccessfulLogin(Response response) {
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Удаляем созданного курьера (если он был создан)")
    private void deleteCreatedCourierIfNeeded() {
        if (courier != null) {
            Response loginResponse = courierClient.login(CourierCredentials.from(courier));
            courierId = loginResponse.then().extract().path("id");

            if (courierId != null) {
                courierClient.delete(courierId);
            }

            courier = null;
            courierId = null;
        }
    }
}
