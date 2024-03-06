import ForUser.Create;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {//не проводил тест без авторизации, т.к. по факту заказ без авторизации проходит
    private static String userToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void createOrderTest(){
        Create create = new Create("vooga1-booga1@yandex.ru", "100500", "Jack");
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));
        userToken = response.jsonPath().getString(    "accessToken");
        userToken = userToken.substring(7);

String ingredients = "{\"ingredients\": [\"61c0c5a71d1f82001bdaaa7a\",\"61c0c5a71d1f82001bdaaa6d\"]}";
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(ingredients)
                .when()
                .post("/api/orders");
        response1.then().statusCode(200)
                .and()
                .body("order._id", notNullValue());
    }

    @Test
    public void createOrderNoIngredientsTest(){
        Create create = new Create("vooga1-booga1@yandex.ru", "100500", "Jack");
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));
        userToken = response.jsonPath().getString(    "accessToken");
        userToken = userToken.substring(7);

        String ingredients = "{\"ingredients\": []}";
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(ingredients)
                .when()
                .post("/api/orders");
        response1.then().statusCode(400)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWrongIngredientsTest(){
        Create create = new Create("vooga1-booga1@yandex.ru", "100500", "Jack");
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));
        userToken = response.jsonPath().getString(    "accessToken");
        userToken = userToken.substring(7);

        String ingredients = "{\"ingredients\": [\"634555555233000F7a\"]}";
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(ingredients)
                .when()
                .post("/api/orders");
        response1.then().statusCode(500);
    }
    @After
    public void deleteUser() {
        baseURI = "https://stellarburgers.nomoreparties.site";
        given()
                .auth().oauth2(userToken)
                .delete("/api/auth/user")
                .then().statusCode(202)
                .and()
                .body("success", equalTo(true));
    }
}
