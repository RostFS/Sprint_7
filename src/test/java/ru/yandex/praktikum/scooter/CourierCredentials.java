package ru.yandex.praktikum.scooter;

public class CourierCredentials {

    private String login;
    private String password;

    public CourierCredentials() {
    }

    public CourierCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public static CourierCredentials from(Courier courier) {
        return new CourierCredentials(courier.getLogin(), courier.getPassword());
    }
}
