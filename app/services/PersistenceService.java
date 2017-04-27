package services;

import org.jetbrains.annotations.NotNull;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PersistenceService {

    private final JPAApi jpaApi;
    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public PersistenceService(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
        entityManagerFactory = Persistence.createEntityManagerFactory("defaultPersistenceUnit");
    }

    @NotNull
    public <T> CompletableFuture<T> asyncWithTransaction(boolean readOnly, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> jpaApi.withTransaction("default", readOnly, supplier));
    }

    public void persist(Object entity) {
        jpaApi.em().persist(entity);
    }

    public void detach(Object entity) {
        jpaApi.em().detach(entity);
    }

    public <T> T readUnique(Class<T> type, long id) {
        return readUnique(type, "id", id);
    }

    public <T> T readUnique(Class<T> type, String field, Object fieldValue) {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(type);
        Root<T> entity = query.from(type);
        query.select(entity);
        query.where(criteriaBuilder.equal(entity.get(field), fieldValue));
        return jpaApi.em().createQuery(query).getSingleResult();
    }

    public <T> Optional<T> readOne(Class<T> type, String field, Object fieldValue) {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(type);
        Root<T> entity = query.from(type);
        query.select(entity);
        query.where(criteriaBuilder.equal(entity.get(field), fieldValue));
        List<T> results = jpaApi.em().createQuery(query).setMaxResults(1).getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }
}
