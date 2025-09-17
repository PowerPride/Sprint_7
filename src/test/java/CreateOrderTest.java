import Client.ApiClient;
import Orders.OrderRequest;
import Courier.User;
import General.Variables;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static Courier.UserLogin.fromUser;
import static Generator.Randomizer.randomUser;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты по созданию заказа (CreateOrder)")
public class CreateOrderTest {
    private ApiClient apiClient = new ApiClient();
    private String id;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = Variables.URL;
    }

    @DisplayName("Создание заказа с разными параметрами")
    @Description("Проверяем создание заказа: корректный запрос возвращает 201 и номер трека заказа. " +
            "Параметры заказа варьируются: имя, адрес, телефон, цвета самоката.")
    @ParameterizedTest
    @CsvSource({
            "Alexey,Papazoglo,Lenina 1,1,89220001111,3,2025-09-20,Please,BLACK",
            "Sergey,Sergeev,Yamskaya 7,3,89310003333,1,2025-09-22,Leave at door,BLACK:GREY",
            "Nikita,Orlov,Polevaya 9,4,89990004444,2,2025-09-23,Call me,"
    })
    void createOrderFullFlow(String firstName,
                             String lastName,
                             String address,
                             String metroStation,
                             String phone,
                             int rentTime,
                             String deliveryDate,
                             String comment,
                             String colors) {

        List<String> colorList;
        if (colors == null || colors.isEmpty()) {
            colorList = null;
        } else if (colors.contains(":")) {
            colorList = Arrays.asList(colors.split(":"));
        } else {
            colorList = Collections.singletonList(colors);
        }

        User user = randomUser();
        Allure.step("Создаём нового пользователя", () ->
        apiClient.createUser(user).then().statusCode(201));

        Response loginResp = Allure.step("Логинимся под созданным пользователем", () ->
                        apiClient.loginUser(fromUser(user)));
        Allure.step("Проверяем, что логин успешен", () ->
                loginResp.then().statusCode(200));
        OrderRequest orderRequest = new OrderRequest(
                firstName, lastName, address, metroStation, phone,
                rentTime, deliveryDate, comment, colorList
        );

        Response resp = Allure.step("Отправляем запрос на создание заказа", () ->
                apiClient.createOrder(orderRequest));

        Allure.step("Проверяем, что заказ создан и вернулся track", () ->
                resp.then().statusCode(201).body("track", notNullValue()));


    }
    //delete
    @AfterEach
    public void tearDown() {
        apiClient.delete(id);
    }
}
