import Client.ApiClient;
import General.Variables;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Тесты по созданию курьера (CreateCourier)")
public class CreateCourierTest {
    private ApiClient apiClient = new ApiClient();
    private String id;
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = Variables.URL;
    }

    /*Сразу хочу сказать мне было интересно попробовать
    реализовать через вытягивание из JSON и просто через строку,
    в последующих тестах все норм=)*/

    //    курьера можно создать;
    //    запрос возвращает правильный код ответа;
    //    успешный запрос возвращает ok: true;
    @DisplayName("Создание нового курьера через JSON-файл — ожидаем 201 и ok")
    @Description("Создание нового курьера через JSON-файл. Проверяем, что запрос возвращает 201 и тело содержит ok.")
    @Test
    public void createNewCourierAndCheckResponse() {
        File user = new File("src/test/resources/newCourier.json");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(Variables.CREATE_COURIER_BASEPATH);
        response.then().assertThat().body("ok", notNullValue())
                .and()
                .statusCode(201);
    }

    //    нельзя создать двух одинаковых курьеров;
    //    если создать пользователя с логином, который уже есть, возвращается ошибка.
    @DisplayName("Попытка создать курьера с уже существующим логином — ожидаем 409")
    @Description("Попытка создать курьера с уже существующим логином. Ожидаем ошибку 409 и месседж.\"Этот логин уже используется. Попробуйте другой.\"")
    @Test
    public void createCourierWithRepeatedLogin() {
        File user = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(Variables.CREATE_COURIER_BASEPATH);

        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    //    чтобы создать курьера, нужно передать в ручку все обязательные поля;
    //    если одного из полей нет, запрос возвращает ошибку;
    @DisplayName("Создать курьера без поля login — ожидаем 400")
    @Description("Пытаемся создать курьера без обязательного поля login. Ожидаем 400 и сообщение о недостаточных данных.")
    @Test
    public void createCourierWithoutRequiredFieldLogin() {
        String json = "{\"password\":\"13234\",\"firstName\":\"sa4ske\"}";
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post(Variables.CREATE_COURIER_BASEPATH);

        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }
    //delete
    @AfterEach
    public void tearDown() {
        apiClient.delete(id);
    }
}

