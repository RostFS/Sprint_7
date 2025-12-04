package ru.yandex.praktikum.scooter;

import java.util.List;

public class Order {

    // Делаем поля public, чтобы Jackson мог их увидеть
    public String firstName;
    public String lastName;
    public String address;
    public String metroStation;
    public String phone;
    public int rentTime;
    public String deliveryDate;
    public String comment;
    public List<String> color;

    // Пустой конструктор (на всякий случай, Jackson его любит)
    public Order() {
    }

    public Order(String firstName,
                 String lastName,
                 String address,
                 String metroStation,
                 String phone,
                 int rentTime,
                 String deliveryDate,
                 String comment,
                 List<String> color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    // Готовый "заказ по умолчанию" с любыми цветами
    public static Order defaultWithColors(List<String> colors) {
        return new Order(
                "ИмяТеста",
                "ФамилияТеста",
                "Москва, Тестовая улица, дом 1",
                "4",                    // id станции метро строкой
                "+79990000000",
                5,                      // срок аренды
                "2025-12-31",           // дата доставки
                "Комментарий к заказу",
                colors
        );
    }
}
