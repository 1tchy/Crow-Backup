package helpers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.db.jpa.JPAApi;
import services.PersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public abstract class WithTransaction extends WithApplication {

    private static EntityManager em;
    private EntityTransaction tx;
    private static JPAApi jpaApi;
    protected PersistenceService persistenceService;

    @BeforeClass
    public static void startDBConnection() {
        jpaApi = spy(app.injector().instanceOf(JPAApi.class));
        em = jpaApi.em("default");
        doReturn(em).when(jpaApi).em();
    }

    @AfterClass
    public static void stopDBConnection() {
        em.close();
        jpaApi.shutdown();
    }

    @Before
    public void startTransaction() {
        tx = em.getTransaction();
        tx.begin();
        persistenceService = new PersistenceService(jpaApi);
    }

    @After
    public void stopTransaction() {
        tx.rollback();
    }

}