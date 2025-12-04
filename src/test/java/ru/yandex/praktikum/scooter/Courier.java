package ru.yandex.praktikum.scooter;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier() {
    }

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    // Удобный фабричный метод для создания рандомного курьера
    public static Courier random() {
        long timestamp = System.currentTimeMillis();
        String login = "test_courier_" + timestamp;
        String password = "password_" + timestamp;
        String firstName = "TestName_" + timestamp;
        return new Courier(login, password, firstName);
    }
}
