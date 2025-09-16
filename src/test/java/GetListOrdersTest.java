import Client.ApiClient;
import Orders.OrderRequest;
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

import java.util.List;

import static Generator.Randomizer.randomUser;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Тесты получения заказа по track")
public class GetListOrdersTest {
    private final ApiClient apiClient = new ApiClient();
    private String id;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = Variables.URL;
    }

    @DisplayName("Создание заказа и получение по его track")
    @Description("Проверяем полный сценарий: создаём пользователя, логинимся, оформляем заказ, " +
            "получаем заказ по track и сверяем поля.")
    @Test
    public void createOrderAndGetByTrack() {
        User user = randomUser();

        Allure.step("Создаём нового курьера", () ->
                apiClient.createUser(user).then().statusCode(201)
        );

        Allure.step("Логинимся под созданным курьером", () ->
                apiClient.loginUser(UserLogin.fromUser(user)).then().statusCode(200)
        );

        List<String> color = List.of("BLACK", "GREY");
        OrderRequest order = new OrderRequest("Ivan", "Ivanov", "Lenina 1", "1", "89990001111",
                3, "2025-09-20", "Please", color);

        Response createResp = Allure.step("Создаём заказ и получаем track", () ->
                apiClient.createOrder(order)
        );
        int track = createResp.then().statusCode(201).extract().path("track");

        Response getResp = Allure.step("Запрашиваем заказ по его track", () ->
                apiClient.getOrderByTrack(track)
        );

        Allure.step("Проверяем, что заказ найден и данные совпадают", () ->
                getResp.then().statusCode(200)
                        .body("order.track", equalTo(track))
                        .body("order.firstName", equalTo(order.getFirstName()))
        );
    }

    @AfterEach
    public void tearDown() {
        apiClient.delete(id);
    }
}















