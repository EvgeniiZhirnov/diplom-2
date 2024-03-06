import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import ForUser.*;
import org.junit.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
private static String userToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
@Test
    public void createUserTest(){
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
public void createRepeatUserTest(){
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

    Response response1 = given()
            .header("Content-type", "application/json")
            .body(create)
            .when()
            .post("/api/auth/register");
    response1.then().statusCode(403)
            .and()
            .body("message", equalTo("User already exists"));
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
