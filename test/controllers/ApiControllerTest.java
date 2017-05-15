package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.WithApplication;
import org.junit.Test;
import play.api.mvc.Call;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.ExecutionException;

import static helpers.GeneralHelpers.assertOptionalEquals;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class ApiControllerTest extends WithApplication {

    @Test
    public void test_UnknownMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("nirvana"), "[\"Welt\"]");
        //Assert
        assertEquals(Http.Status.NOT_FOUND, actual.status());
    }

    @Test
    public void test_simpleMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "[\"Welt\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        assertOptionalEquals("application/json", actual.contentType());
        assertEquals("\"Hallo Welt\"", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_badButValidJson() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "{}");
        //Assert
        assertEquals(Http.Status.BAD_REQUEST, actual.status());
        assertEquals("{} ist ungültig", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_badButValidJsonThatIsStillAnArray() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "[{}]");
        //Assert
        assertEquals(Http.Status.BAD_REQUEST, actual.status());
        assertEquals("[{}] ist ungültig für helloWorld", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_invalidJson() throws ExecutionException, InterruptedException {
        //Arrange
        Call target = controllers.routes.ApiController.apiCall("helloWorld");
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder();
        //Act
        Result actual = route(requestBuilder.method(target.method()).uri(target.url()));
        //Assert
        assertNotEquals(Http.Status.OK, actual.status());
    }

    @Test
    public void test_simpleMethodCall_when_inputAndReturnIsNull() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "[null]");
        //Assert
        assertEquals(Http.Status.NO_CONTENT, actual.status());
    }

    @Test
    public void test_simple_createUser() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("createUser"), "[\"test@test.com\", \"1234\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        JsonNode actualJson = contentAsJson(actual);
        assertTrue(actualJson.get("id").isNumber());
        assertEquals("test@test.com", actualJson.get("mail").asText());
        assertTrue(actualJson.get("passwordHash").isNull());
    }

    @Test
    public void test_simple_login() {
        //Arrange
        simulateJsonRequest(controllers.routes.ApiController.apiCall("createUser"), "[\"test1@test.com\", \"1234\"]");
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("login"), "[\"test1@test.com\", \"1234\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        JsonNode actualJson = contentAsJson(actual);
        assertThat(actualJson.asText(), startsWith("Token "));
    }

    @Test
    public void test_login_withWrongPassword() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("login"), "[\"test1@test.com\", \"falsch\"]");
        //Assert
        assertNotEquals(Http.Status.OK, actual.status());
    }

    @Test
    public void test_changeUserPassword_whenNotLogedIn() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("changeUserPassword"), "[\"abcd\"]");
        //Assert
        assertEquals(Http.Status.FORBIDDEN, actual.status());
    }


    @Test
    public void test_changeUserPassword() throws ExecutionException, InterruptedException {
        requestWithUser(controllers.routes.ApiController.apiCall("changeUserPassword"), "[\"abcd\"]", (user, actual) -> {
            //Assert
            assertEquals(Http.Status.NO_CONTENT, actual.status());
            jpaApi.em().refresh(user);
            assertNotEquals("#####", user.getPasswordHash());
        });
    }

    @Test
    public void test_logout() {
        requestWithUser(controllers.routes.ApiController.apiCall("logout"), "[]", (user, actual) -> {
            assertEquals(Http.Status.NO_CONTENT, actual.status());
        });
    }

    @Test
    public void test_getCurrentUser() {
        requestWithUser(controllers.routes.ApiController.apiCall("getCurrentUser"), "[]", (user, actual) -> {
            assertEquals(Http.Status.OK, actual.status());
            assertEquals(Json.stringify(Json.toJson(user)), Json.stringify(contentAsJson(actual)));
        });
    }

    private JsonNode contentAsJson(Result result) {
        assertOptionalEquals("application/json", result.contentType());
        return Json.parse(contentAsString(result));
    }

}