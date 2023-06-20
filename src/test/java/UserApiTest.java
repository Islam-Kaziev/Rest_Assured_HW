import io.qameta.allure.Owner;
import lombok.User;
import lombok.UserData;
import lombok.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.Specs.*;

public class UserApiTest {

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Проверка наличия почты")
    void checkSingleEmailLombok() {
        UserData data = step("Проверить наличие почты", () ->
                given(request)
                        .when()
                        .get("/users/2")
                        .then()
                        .log().body()
                        .spec(responseOk200)
                        .extract().as(UserData.class));

        step("Проверить, что в ответе нужная почта", () ->
                assertEquals("janet.weaver@reqres.in", data.getUser().getEmail()));
    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Проверка имени и фамилии пользователя")
    void checkSingleNameLombok() {
        UserData data = step("Проверить имя и фамилию пользователя", () ->
                given(request)
                        .when()
                        .get("/users/2")
                        .then()
                        .spec(responseOk200)
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
                given(request)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .spec(responseOk200))
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
                given(request)
                        .body(userBody)
                        .when()
                        .post("/users")
                        .then()
                        .spec(responseCreated201)
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
                given(request)
                        .body(userBody)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(responseOk200)
                        .extract().as(UserResponse.class));

        step("Проверить имя пользователя", () ->
                assertThat(response.getName()).isEqualTo("morpheus"));
        step("Проверить новое значение в поле работа", () ->
                assertThat(response.getJob()).isEqualTo("zion resident"));

    }

    @Test
    @Owner("Казиев Ислам")
    @DisplayName("Удаление данных")
    void deleteUser() {
        step("Запрос на удаление", () ->
                given(request)
                        .when()
                        .delete("/users/2")
                        .then()
                        .spec(responseDelete204));
    }
}
