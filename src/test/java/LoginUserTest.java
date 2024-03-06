import ForUser.Create;
import ForUser.Login;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    private static String userToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void LoginTest() {
        Create create = new Create("vooga1-booga1@yandex.ru", "100500", "Jack");
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));
        userToken = response.jsonPath().getString("accessToken");
        userToken = userToken.substring(7);
        System.out.println(userToken);

        Login login = new Login("vooga1-booga1@yandex.ru", "100500");
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login");
        response1.then().statusCode(200);
    }

    @Test
    public void LoginWrongInfoTest() {
        Create create = new Create("vooga1-booga1@yandex.ru", "100500", "Jack");
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));
        userToken = response.jsonPath().getString("accessToken");
        userToken = userToken.substring(7);
        System.out.println(userToken);

        Login login = new Login("voga-boga@yandex.ru", "1234456");
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/auth/login");
        response1.then().statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
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

