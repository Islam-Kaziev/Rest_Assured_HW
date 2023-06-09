import io.qameta.allure.restassured.AllureRestAssured;
import lombok.User;
import lombok.UserData;
import lombok.UserResponse;
import org.junit.jupiter.api.Test;
import specs.Specs;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.Specs.request;

public class ReqresTest {

    @Test
    void checkSingleEmailLombok() {
        UserData data = step("Check single email", () ->
                given()
                .filter(withCustomTemplates())
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .log().body()
                .spec(Specs.responseOk)
                .extract().as(UserData.class));

        step("Check list email", () ->
        assertEquals("janet.weaver@reqres.in", data.getUser().getEmail()));
    }

    @Test
    void checkSingleNameLombok() {
        UserData data = step("Check single name", () ->
                given()
                .filter(withCustomTemplates())
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .spec(Specs.responseOk)
                .log().body()
                .extract().as(UserData.class));
        step("Check list name", () ->
        assertEquals("Janet", data.getUser().getFirstName()));
        step("Check list lastname", () ->
        assertEquals("Weaver", data.getUser().getLastName()));
    }

    @Test
    void listUserTest() {
        step("Check list user", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .when()
                .get("/users?page=2")
                .then()
                .spec(Specs.responseOk))
                .body("total", is(12))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"))
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Test
    void createUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("leader");
        UserResponse response = step("Create new user", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/users")
                .then()
                .spec(Specs.responseCreated)
                .extract().as(UserResponse.class));
        step("Check name", () ->
        assertThat(response.getName()).isEqualTo("morpheus"));
        step("Check job", () ->
        assertThat(response.getJob()).isEqualTo("leader"));
    }

    @Test
    void updateUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("zion resident");
        UserResponse response = step("Update user data", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .put("/users/2")
                .then()
                .spec(Specs.responseOk)
                .extract().as(UserResponse.class));

        step("Check name", () ->
        assertThat(response.getName()).isEqualTo("morpheus"));
        step("Check job", () ->
        assertThat(response.getJob()).isEqualTo("zion resident"));

    }

    @Test
    void loginSuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("eve.holt@reqres.in");
        userBody.setPassword("cityslicka");
        UserResponse response = step("Successful login", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseOk)
                .extract().as(UserResponse.class));

        step("Check token", () ->
        assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4"));
    }

    @Test
    void loginUnsuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("peter@klaven");

        UserResponse response = step("Unsuccessful login", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseBadRequest)
                .extract().as(UserResponse.class));

        step("Check displayed error", () ->
        assertThat(response.getError()).isEqualTo("Missing password"));
    }
}
