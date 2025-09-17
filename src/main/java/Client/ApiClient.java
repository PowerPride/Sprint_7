package Client;

import Orders.OrderRequest;
import Courier.User;

import Courier.UserLogin;
import General.Variables;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(Variables.CREATE_COURIER_BASEPATH);
    }

    public Response loginUser(UserLogin userLogin) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(userLogin)
                .when()
                .post(Variables.LOGIN_COURIER_BASEPATH);
    }

    public Response createOrder(OrderRequest orderRequest) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(orderRequest)
                .when()
                .post(Variables.CREATE_ORDER_BASEPATH);
    }

    public Response delete(String id) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(Variables.DELETE_COURIER_BASEPATH + "/" + id);
    }

    public Response getOrderByTrack(int track) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track");
    }

}