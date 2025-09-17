import Client.ApiClient;
import Courier.User;
import Courier.UserLogin;
import General.Variables;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static Courier.UserLogin.fromUser;
import static Generator.Randomizer.randomUser;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты авторизации курьера (LoginCourier)")
public class LoginCourierTest {
    private ApiClient apiClient = new ApiClient();
    private String id;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = Variables.URL;
    }

    @DisplayName("Успешная авторизация курьера — возвращается id")
    @Description("Создаём случайного курьера, затем логинимся под ним. Ожидаем, что ответ содержит поле id и код 200.")
    @Test
    public void loginNewCourierAndCheckResponse() {
        User user = randomUser();

        // создание курьера
        Response createResponse = Allure.step("Создаём курьера через API", () ->
                apiClient.createUser(user)
        );
        Allure.step("Проверяем, что создание вернуло 201", () ->
                createResponse.then().statusCode(201)
        );

        // логинимся
        Response loginResponse = Allure.step("Отправляем запрос на логин курьера", () ->
                apiClient.loginUser(fromUser(user))
        );

        Allure.step("Проверяем, что в ответе есть id и статус 200", () ->
                loginResponse.then().assertThat()
                        .body("id", notNullValue())
                        .statusCode(200)
        );
    }

    @DisplayName("Авторизация без одного обязательного поля — ожидаем 400")
    @Description("Создаём курьера, затем пытаемся залогиниться без пароля (пустая строка). Ожидаем 400 и сообщение о недостаточных данных.")
    @Test
    public void loginNewCourierWithoutOneField() {
        User user = randomUser();

        Response createResponse = Allure.step("Создаём курьера через API", () ->
                apiClient.createUser(user)
        );
        Allure.step("Проверяем, что создание вернуло 201", () ->
                createResponse.then().statusCode(201)
        );

        // логинимся с пустым паролем
        UserLogin loginPayload = new UserLogin(user.getLogin(), "");
        Response loginResponse = Allure.step("Пытаемся залогиниться с пустым паролем", () ->
                apiClient.loginUser(loginPayload)
        );

        Allure.step("Проверяем, что пришёл 400 и сообщение 'Недостаточно данных для входа'", () ->
                loginResponse.then()
                        .statusCode(400)
                        .body("message", equalTo("Недостаточно данных для входа"))
        );
    }

    @DisplayName("Авторизация с неверным паролем — ожидаем 404")
    @Description("Создаём курьера, затем пытаемся залогиниться с неправильным паролем. Ожидаем 404 и сообщение, что учетная запись не найдена.")
    @Test
    public void loginNewCourierWithWrong() {
        User user = randomUser();

        Response createResponse = Allure.step("Создаём курьера через API", () ->
                apiClient.createUser(user)
        );
        Allure.step("Проверяем, что создание вернуло 201", () ->
                createResponse.then().statusCode(201)
        );

        // логинимся с неправильным паролем
        UserLogin loginPayload = new UserLogin(user.getLogin(), "234252");
        Response loginResponse = Allure.step("Пытаемся залогиниться с неверным паролем", () ->
                apiClient.loginUser(loginPayload)
        );

        Allure.step("Проверяем, что пришёл 404 и сообщение 'Учетная запись не найдена'", () ->
                loginResponse.then()
                        .statusCode(404)
                        .body("message", equalTo("Учетная запись не найдена"))
        );
    }

    @AfterEach
    public void tearDown() {
        apiClient.delete(id);
    }
}
