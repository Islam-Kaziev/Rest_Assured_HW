import io.qameta.allure.Owner;
import lombok.User;
import lombok.UserData;
import lombok.UserResponse;
import org.junit.jupiter.api.DisplayName;
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
    @Owner("Казиев Ислам")
    @DisplayName("Проверка наличия почты")
    void checkSingleEmailLombok() {
        UserData data = step("Проверить наличие почты", () ->
                given()
                .filter(withCustomTemplates())
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .log().body()
                .spec(Specs.responseOk)
                .extract().as(UserData.class));

        step("Проверить, что в ответе нужная почта", () ->
        assertEquals("janet.weaver@reqres.in", data.getUser().getEmail()));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Проверка имени и фамилии пользователя")
    void checkSingleNameLombok() {
        UserData data = step("Проверить имя и фамилию пользователя", () ->
                given()
                .filter(withCustomTemplates())
                .spec(Specs.request)
                .when()
                .get("/users/2")
                .then()
                .spec(Specs.responseOk)
                .log().body()
                .extract().as(UserData.class));
        step("Проверить имя", () ->
        assertEquals("Janet", data.getUser().getFirstName()));
        step("Проверить фамилию", () ->
        assertEquals("Weaver", data.getUser().getLastName()));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Проверка вывода текста и урла для раздела поддержки")
    void listUserTest() {
        step("Провить вывод текста и урла для раздела поддержки", () ->
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
    @Owner("Казиев Ислам")
    @DisplayName("Создание нового пользователя")
    void createUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("leader");
        UserResponse response = step("Создать нового пользователя", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/users")
                .then()
                .spec(Specs.responseCreated)
                .extract().as(UserResponse.class));
        step("Проверить имя созданного пользователя", () ->
        assertThat(response.getName()).isEqualTo("morpheus"));
        step("Проверить поле работа у нового пользователя", () ->
        assertThat(response.getJob()).isEqualTo("leader"));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Обновление данных пользователя")
    void updateUserTestLombok() {
        User userBody = new User();
        userBody.setName("morpheus");
        userBody.setJob("zion resident");
        UserResponse response = step("Обновить данные пользователя", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .put("/users/2")
                .then()
                .spec(Specs.responseOk)
                .extract().as(UserResponse.class));

        step("Проверить имя пользователя", () ->
        assertThat(response.getName()).isEqualTo("morpheus"));
        step("Проверить новое значение в поле работа", () ->
        assertThat(response.getJob()).isEqualTo("zion resident"));

    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Успешная авторизация пользователя")
    void loginSuccessfulTest() {
        User userBody = new User();
        userBody.setEmail("eve.holt@reqres.in");
        userBody.setPassword("cityslicka");
        UserResponse response = step("Успешная авторизация", () ->
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseOk)
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
                given()
                .filter(withCustomTemplates())
                .spec(request)
                .body(userBody)
                .when()
                .post("/login")
                .then()
                .spec(Specs.responseBadRequest)
                .extract().as(UserResponse.class));

        step("Проверить наличие текста ошибки", () ->
        assertThat(response.getError()).isEqualTo("Missing password"));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Удаление данных")
    void deleteUser() {
        step("Запрос на удаление", () ->
                        given()
                        .filter(withCustomTemplates())
                        .spec(request)
                        .when()
                        .delete("/users/2")
                        .then()
                        .spec(Specs.responseDelete));
    }
}
