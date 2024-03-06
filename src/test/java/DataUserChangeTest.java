import ForUser.Change;
import ForUser.Create;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class DataUserChangeTest {
    private static String userToken;

    public String email;
    public  String name;
    public DataUserChangeTest(String email, String name){
        this.email = email;
        this.name = name;
    }
    @Parameterized.Parameters
    public static Object[][] getExpected() {
        return new Object[][]{
                {"vooga1-booga1@yandex.ru", "Black"},
                {"boobietheroocky@yandex.ru", "Jack"},
                {"boobietheroocky@yandex.ru", "Black"}
        };
    }
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void changeUserTest(){
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

        Change change = new Change(email, name);
        Response response1 = given()
                .auth().oauth2(userToken)
                .header("Content-type", "application/json")
                .body(change)
                .when()
                .patch("/api/auth/user");
        response1.then().statusCode(200)
                .and()
                .body("user.name", equalTo(name))
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    public void changeUserNotAuthTest(){
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

        Change change = new Change(email, name);
        Response response1 = given()
                .header("Content-type", "application/json")
                .body(change)
                .when()
                .patch("/api/auth/user");
        response1.then().statusCode(401)
                .and()
                .body("message", equalTo("You should be authorised"));
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
