package controllers.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.WithApplication;
import models.interfaces.FriendshipServerInterface;
import models.user.FriendLink;
import models.user.Friendship;
import models.user.User;
import org.junit.Test;
import play.mvc.Http;

import static org.junit.Assert.*;
import static play.test.Helpers.contentAsString;

public class FriendshipApiControllerTest extends WithApplication {

    @Test
    public void test_simple_createUser() {
        requestWithUser(
                user -> {
                    //Arrange
                    User otherUser = new User();
                    otherUser.setMail("tets2@test.com");
                    return persist(otherUser);
                },
                routes.BaseApiController.apiCall(FriendshipServerInterface.class.getSimpleName(), "addFriend"),
                otherUser -> "[{\"id\": " + otherUser.getId() + "}]",
                ((user, otherUser, result) -> {
                    //Assert
                    assertEquals(Http.Status.NO_CONTENT, result.status());
                }));
    }

    @Test
    public void test_simple_openRequests() {
        requestWithUser(routes.BaseApiController.apiCall(FriendshipServerInterface.class.getSimpleName(), "openRequests"), "[]",
                ((user, result) -> {
                    //Assert
                    assertEquals(Http.Status.OK, result.status());
                    String json = contentAsString(result);
                    assertEquals("[]", json);
                }));
    }

    @Test
    public void test_openRequests_withOneOpenRequest() {
        requestWithUser(user -> {
                    //Arrange
                    User otherUser = new User();
                    otherUser.setMail("tets2@test.com");
                    persist(otherUser, new Friendship(persist(new FriendLink(otherUser, user))));
                    return otherUser;
                }, routes.BaseApiController.apiCall(FriendshipServerInterface.class.getSimpleName(), "openRequests"), "[]",
                ((user, otherUser, result) -> {
                    //Assert
                    assertEquals(Http.Status.OK, result.status());
                    JsonNode actualJson = contentAsJson(result);
                    assertTrue(actualJson.isArray());
                    assertEquals(1, actualJson.size());
                    JsonNode actualJsonElement = actualJson.get(0);
                    assertFalse(actualJsonElement.get("confirmed").asBoolean());
                    assertTrue(actualJsonElement.get("links").isArray());
                    assertEquals(1, actualJsonElement.get("links").size());
                    assertEquals("tets2@test.com", actualJsonElement.get("links").get(0).get("from").get("mail").asText());
                    assertEquals(user.getMail(), actualJsonElement.get("links").get(0).get("to").get("mail").asText());
                }));
    }

    @Test
    public void test_simple_list() {
        requestWithUser(routes.BaseApiController.apiCall(FriendshipServerInterface.class.getSimpleName(), "list"), "[]",
                ((user, result) -> {
                    //Assert
                    assertEquals(Http.Status.OK, result.status());
                    String json = contentAsString(result);
                    assertEquals("[]", json);
                }));
    }

    @Test
    public void test_list_withOneOpenRequest() {
        requestWithUser(user -> {
                    //Arrange
                    User otherUser = new User();
                    otherUser.setMail("tets2@test.com");
                    persist(otherUser, new Friendship(persist(new FriendLink(otherUser, user)), persist(new FriendLink(user, otherUser))));
                    return otherUser;
                }, routes.BaseApiController.apiCall(FriendshipServerInterface.class.getSimpleName(), "list"), "[]",
                ((user, otherUser, result) -> {
                    //Assert
                    assertEquals(Http.Status.OK, result.status());
                    JsonNode actualJson = contentAsJson(result);
                    assertTrue(actualJson.isArray());
                    assertEquals(1, actualJson.size());
                    JsonNode actualJsonElement = actualJson.get(0);
                    assertTrue(actualJsonElement.get("confirmed").asBoolean());
                    assertTrue(actualJsonElement.get("links").isArray());
                    assertEquals(2, actualJsonElement.get("links").size());
                }));
    }

}