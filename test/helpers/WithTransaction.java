package helpers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.Application;
import play.db.jpa.JPAApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashMap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static play.test.Helpers.inMemoryDatabase;

public abstract class WithTransaction {

    private static Application app;
    private static EntityManager em;
    private EntityTransaction tx;
    protected static JPAApi jpaApi;

    @BeforeClass
    public static void startPlay() {
        app = new GuiceApplicationBuilder().configure(new HashMap<>(inMemoryDatabase())).build();
        jpaApi = spy(app.injector().instanceOf(JPAApi.class));
        em = jpaApi.em("default");
        doReturn(em).when(jpaApi).em();
    }

    @AfterClass
    public static void stopPlay() {
        em.close();
        jpaApi.shutdown();
        Helpers.stop(app);
    }


    @Before
    public void startTransaction() {
        tx = em.getTransaction();
        tx.begin();
    }

    @After
    public void stopTransaction() {
        tx.rollback();
    }

}