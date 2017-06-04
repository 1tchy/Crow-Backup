package services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import play.db.jpa.JPAApi;
import play.libs.F;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class PersistenceService {

    private final JPAApi jpaApi;

    @Inject
    public PersistenceService(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @NotNull
    public <T> CompletionStage<T> asyncWithTransaction(boolean readOnly, Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> jpaApi.withTransaction("default", readOnly, supplier));
    }

    public CompletionStage<Void> asyncWithTransaction(Runnable runnable) {
        return asyncWithTransaction(false, () -> {
            runnable.run();
            return null;
        });
    }

    public void persist(Object... entities) {
        for (Object entity : entities) {
            jpaApi.em().persist(entity);
        }
    }

    public void detach(Object entity) {
        jpaApi.em().detach(entity);
    }

    public <T> T readUnique(Class<T> type, long id) {
        Optional<T> t = readOne(type, id);
        if (!t.isPresent()) {
            throw new NoResultException("No " + type + " for ID " + id);
        }
        return t.get();
    }

    public <T> Optional<T> readOne(Class<T> type, long id) {
        return Optional.ofNullable(jpaApi.em().find(type, id));
    }

    public <T> Optional<T> readOne(Class<T> type, F.Tuple<String, Object> whereFieldHasValue) {
        if (!whereFieldHasValue._1.matches("\\w+")) {//for security reasons (helps preventing SQL injection)
            throw new RuntimeException(whereFieldHasValue._1 + " is not valid");
        }
        String query = "SELECT t FROM " + type.getName() + " AS t WHERE " + whereFieldHasValue._1 + " = ?1";
        return readOne(type, query, whereFieldHasValue._2);
    }

    public <T> Optional<T> readOne(Class<T> type, String query, Object... params) {
        List<T> resultList = read(type, query, 1, params);
        if (resultList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultList.get(0));
        }
    }

    public <T> List<T> read(Class<T> type, String query, @Nullable Integer limit, @NotNull Object... params) {
        TypedQuery<T> q = jpaApi.em().createQuery(query, type);
        for (int i = 1; i <= params.length; i++) {
            q.setParameter(i, params[i - 1]);
        }
        if (limit != null) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    public void remove(Object entity) {
        jpaApi.em().remove(entity);
    }

}
