import lombok.UserData;
import org.junit.jupiter.api.Test;
import specs.Specs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.Specs.*;

public class ReqresTest {

    @Test
    void checkSingleEmailLombok() {
        UserData data = given()
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .log().body()
                .spec(Specs.responseOk)
                .extract().as(UserData.class);
        assertEquals("janet.weaver@reqres.in", data.getUser().getEmail());
    }

    @Test
    void checkSingleNameLombok() {
        UserData data = given()
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .spec(Specs.responseOk)
                .log().body()
                .extract().as(UserData.class);
        assertEquals("Janet", data.getUser().getFirstName());
        assertEquals("Weaver", data.getUser().getLastName());
    }

    @Test
    void listUserTest() {
        given()
                .spec(request)
                .when()
                .get("/users?page=2")
                .then()
                .spec(Specs.responseOk)
                .body("total", is(12))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"))
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Test
    void createUserTest() {
        String body = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";
                given()
                .spec(request)
                .body(body)
                .when()
                .post("/users")
                .then()
                .spec(Specs.responseCreated)
                .body("name", is("morpheus"))
                .body("job", is("leader"));
    }

    @Test
    void updateUserTest() {
        String body = "{ \"name\": \"morpheus\", \"job\": \"zion resident\" }";
                given()
                .spec(request)
                .body(body)
                .when()
                .put("/users/2")
                .then()
                .spec(Specs.responseOk)
                .body("name", is("morpheus"))
                .body("job", is("zion resident"));

    }

    @Test
    void loginSuccessfulTest() {
        String body = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";
        given()
                .spec(request)
                .body(body)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseOk)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void loginUnsuccessfulTest() {
        String body = "{ \"email\": \"peter@klaven\" }";
        given()
                .spec(request)
                .body(body)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseBadRequest)
                .body("error", is("Missing password"));
    }
}
