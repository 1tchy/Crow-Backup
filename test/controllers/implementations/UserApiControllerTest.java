package controllers.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.WithApplication;
import models.interfaces.UserServerInterface;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

public class UserApiControllerTest extends WithApplication {

    @Test
    public void test_simple_createUser() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "createUser"), "[\"test@test.com\", \"1234\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        JsonNode actualJson = contentAsJson(actual);
        assertTrue(actualJson.get("id").isNumber());
        assertEquals("test@test.com", actualJson.get("mail").asText());
        assertNull(actualJson.get("passwordHash"));
    }

    @Test
    public void test_simple_login() {
        //Arrange
        simulateJsonRequest(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "createUser"), "[\"test1@test.com\", \"1234\"]");
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "login"), "[\"test1@test.com\", \"1234\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        JsonNode actualJson = contentAsJson(actual);
        assertThat(actualJson.asText(), startsWith("Token "));
    }

    @Test
    public void test_login_withWrongPassword() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "login"), "[\"test1@test.com\", \"falsch\"]");
        //Assert
        assertNotEquals(Http.Status.OK, actual.status());
    }

    @Test
    public void test_changeUserPassword_whenNotLogedIn() {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "changeUserPassword"), "[\"abcd\"]");
        //Assert
        assertEquals(Http.Status.FORBIDDEN, actual.status());
    }


    @Test
    public void test_changeUserPassword() throws ExecutionException, InterruptedException {
        requestWithUser(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "changeUserPassword"), "[\"abcd\"]", (user, actual) -> {
            //Assert
            assertEquals(Http.Status.NO_CONTENT, actual.status());
            jpaApi.em().refresh(user);
            assertNotEquals("#####", user.getPasswordHash());
        });
    }

    @Test
    public void test_logout() {
        requestWithUser(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "logout"), "[]", (user, actual) -> {//
            assertEquals(Http.Status.NO_CONTENT, actual.status());
        });
    }

    @Test
    public void test_getCurrentUser() {
        requestWithUser(routes.BaseApiController.apiCall(UserServerInterface.class.getSimpleName(), "getCurrentUser"), "[]", (user, actual) -> {
            assertEquals(Http.Status.OK, actual.status());
            assertEquals(Json.stringify(Json.toJson(user)), Json.stringify(contentAsJson(actual)));
        });
    }

}