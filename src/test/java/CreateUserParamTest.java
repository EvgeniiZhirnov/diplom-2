import ForUser.Create;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserParamTest {
    public String email;
    public String password;
    public  String name;
    public CreateUserParamTest(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }
    @Parameterized.Parameters
    public static Object[][] getExpected() {
        return new Object[][]{
                {"", "100500", "Jack"},
                {"vooga1-booga@yandex.ru", "", "Jack"},
                {"vooga1-booga@yandex.ru", "100500", ""},
                {"", "", ""}
        };
    }
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    public void createWrongUserTest(){
        Create create = new Create(email, password, name);
        Response response = given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/auth/register");
        response.then().statusCode(403)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
        }