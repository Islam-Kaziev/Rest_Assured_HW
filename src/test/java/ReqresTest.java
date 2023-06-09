import lombok.User;
import lombok.UserData;
import lombok.UserResponse;
import org.junit.jupiter.api.Test;
import specs.Specs;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void createUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("leader");
        UserResponse response = given()
                .spec(request)
                .body(userBody)
                .when()
                .post("/users")
                .then()
                .spec(Specs.responseCreated)
                .extract().as(UserResponse.class);
        assertThat(response.getName()).isEqualTo("morpheus");
        assertThat(response.getJob()).isEqualTo("leader");
    }

    @Test
    void updateUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("zion resident");
        UserResponse response = given()
                .spec(request)
                .body(userBody)
                .when()
                .put("/users/2")
                .then()
                .spec(Specs.responseOk)
                .extract().as(UserResponse.class);
        assertThat(response.getName()).isEqualTo("morpheus");
        assertThat(response.getJob()).isEqualTo("zion resident");

    }

    @Test
    void loginSuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("eve.holt@reqres.in");
        userBody.setPassword("cityslicka");
        UserResponse response = given()
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseOk)
                .extract().as(UserResponse.class);
        assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
    }

    @Test
    void loginUnsuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("peter@klaven");
        UserResponse response = given()
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseBadRequest)
                .extract().as(UserResponse.class);
        assertThat(response.getError()).isEqualTo("Missing password");
    }
}
