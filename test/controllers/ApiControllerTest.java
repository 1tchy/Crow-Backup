package controllers;

import org.junit.Test;
import play.api.mvc.Call;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.concurrent.ExecutionException;

import static helpers.GeneralHelpers.assertOptionalEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class ApiControllerTest extends WithApplication {

    @Test
    public void test_UnknownMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("nirvana"), "\"Welt\"");
        //Assert
        assertEquals(Http.Status.NOT_FOUND, actual.status());
    }

    @Test
    public void test_simpleMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "\"Welt\"");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        assertOptionalEquals("application/json", actual.contentType());
    }

    @Test
    public void test_simpleMethodCall_with_badButValidJson() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(controllers.routes.ApiController.apiCall("helloWorld"), "{}");
        //Assert
        assertEquals(Http.Status.BAD_REQUEST, actual.status());
        assertEquals("{} ist ungültig für helloWorld", contentAsString(actual));
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

    private Result simulateJsonRequest(Call target, String json) {
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder().bodyJson(Json.parse(json));
        return route(requestBuilder.method(target.method()).uri(target.url()));
    }

}