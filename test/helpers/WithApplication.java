package helpers;

import models.user.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.api.mvc.Call;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import services.LoginTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;

public class WithApplication {

    protected static Application app;
    protected static JPAApi jpaApi;

    @BeforeClass
    public static void startPlay() {
        Map<String, String> dbSettings = new HashMap<>();
        dbSettings.put("MODE", "PostgreSQL");
        dbSettings.put("DB_CLOSE_DELAY", "-1");
        HashMap<String, Object> conf = new HashMap<>(inMemoryDatabase("default", dbSettings));
        conf.put("jpa.default", "testPersistenceUnit");
        System.out.println("conf.get(\"db.default.url\") = " + conf.get("db.default.url"));
        app = new GuiceApplicationBuilder().configure(conf).build();
        Helpers.start(app);
        jpaApi = app.injector().instanceOf(JPAApi.class);
    }

    @AfterClass
    public static void stopPlay() {
        Helpers.stop(app);
    }

    protected static Result simulateJsonRequest(Call target, String json) {
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder().bodyJson(Json.parse(json));
        return route(app, requestBuilder.method(target.method()).uri(target.url()));
    }

    protected static Result simulateJsonRequest(Call target, String json, String authenticatedUserId) {
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder().bodyJson(Json.parse(json));
        requestBuilder.header("Authentication", app.injector().instanceOf(LoginTokenService.class).create(authenticatedUserId));
        return route(app, requestBuilder.method(target.method()).uri(target.url()));
    }

    /**
     * Test mit drei Teilen, die teilweise eine aktive Transaktion haben
     *
     * @param arrange:  1. Teil; Bereitet den Test vor, hat eine aktive Transaktion (zum Testdaten aufsetzen)
     * @param act:      2. Teil; Effektiver Test, hat keine eigene Transaktion (zum z.B. simulateJsonRequest() aufrufen)
     * @param asserter: 3. Teil; Überprüft den Test, hat wieder eine aktive Transaktion (zum Verifizieren von Daten in der Datenbank)
     */
    protected static <T> void runThreeStepTest(Supplier<T> arrange, Function<T, Result> act, BiConsumer<T, Result> asserter) {
        jpaApi.withTransaction(() -> {
            //Arrange
            T t = arrange.get();
            jpaApi.em().getTransaction().commit();
            //Act
            Result actual = act.apply(t);
            //Assert
            jpaApi.em().getTransaction().begin();
            asserter.accept(t, actual);
        });
    }

    protected static void requestWithUser(Call target, String json, BiConsumer<User, Result> asserter) {
        runThreeStepTest(() -> {
            //Arrange
            User user = new User();
            user.setMail("tets3@test.com");
            user.setPasswordHash("#####");
            jpaApi.em().persist(user);
            return user;
        }, user -> {
            //Act
            return simulateJsonRequest(target, json, user.getId() + "");
        }, ((user, result) -> {
            //Assert
            //noinspection Convert2MethodRef
            asserter.accept(user, result);
        }));
    }

}
