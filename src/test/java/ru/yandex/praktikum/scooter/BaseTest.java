package ru.yandex.praktikum.scooter;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        // Базовый URL для всех запросов
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }
}
