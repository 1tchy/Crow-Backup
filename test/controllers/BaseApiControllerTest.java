package controllers;

import helpers.WithApplication;
import org.junit.Test;
import play.api.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.ExecutionException;

import static helpers.GeneralHelpers.assertOptionalEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class BaseApiControllerTest extends WithApplication {

    @Test
    public void test_UnknownClassCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("ExistiertNicht", "nirvana"), "[\"Welt\"]");
        //Assert
        assertEquals(Http.Status.NOT_FOUND, actual.status());
    }

    @Test
    public void test_UnknownMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("TestServerInterface", "nirvana"), "[\"Welt\"]");
        //Assert
        assertEquals(Http.Status.NOT_FOUND, actual.status());
    }

    @Test
    public void test_simpleMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("TestServerInterface", "helloWorld"), "[\"Welt\"]");
        //Assert
        assertEquals(Http.Status.OK, actual.status());
        assertOptionalEquals("application/json", actual.contentType());
        assertEquals("\"Hallo Welt\"", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_badButValidJson() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("TestServerInterface", "helloWorld"), "{}");
        //Assert
        assertEquals(Http.Status.BAD_REQUEST, actual.status());
        assertEquals("{} ist ungültig", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_badButValidJsonThatIsStillAnArray() throws ExecutionException, InterruptedException {
        //Arrange
        //Act
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("TestServerInterface", "helloWorld"), "[{}]");
        //Assert
        assertEquals(Http.Status.BAD_REQUEST, actual.status());
        assertEquals("[{}] ist ungültig für TestServerInterface/helloWorld", contentAsString(actual));
    }

    @Test
    public void test_simpleMethodCall_with_invalidJson() throws ExecutionException, InterruptedException {
        //Arrange
        Call target = routes.BaseApiController.apiCall("TestServerInterface", "helloWorld");
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
        Result actual = simulateJsonRequest(routes.BaseApiController.apiCall("TestServerInterface", "helloWorld"), "[null]");
        //Assert
        assertEquals(Http.Status.NO_CONTENT, actual.status());
    }

}