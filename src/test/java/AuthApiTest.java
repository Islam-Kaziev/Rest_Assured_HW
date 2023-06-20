import io.qameta.allure.Owner;
import lombok.User;
import lombok.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static specs.Specs.*;

public class AuthApiTest {

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Успешная авторизация пользователя")
    void loginSuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("eve.holt@reqres.in");
        userBody.setPassword("cityslicka");
        UserResponse response = step("Успешная авторизация", () ->
                given(request)
                        .body(userBody)
                        .when()
                        .post("/login")
                        .then()
                        .spec(responseOk200)
                        .extract().as(UserResponse.class));

        step("Проверить токен", () ->
                assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Неуспешная авторизация пользователя")
    void loginUnsuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("peter@klaven");

        UserResponse response = step("Неуспешная авторизация", () ->
                given(request)
                        .body(userBody)
                        .when()
                        .post("/login")
                        .then()
                        .spec(responseBadRequest400)
                        .extract().as(UserResponse.class));

        step("Проверить наличие текста ошибки", () ->
                assertThat(response.getError()).isEqualTo("Missing password"));
    }
}
