package controllers.implementations;

import controllers.actions.AuthenticatedRequest;
import controllers.actions.WithUser;
import models.interfaces.UserServerInterface;
import models.user.User;
import play.libs.F;
import play.mvc.Controller;
import services.LoginTokenService;
import services.PasswordService;
import services.PersistenceService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserApiController implements UserServerInterface {

    private final PersistenceService persistenceService;
    private final PasswordService passwordService;
    private final LoginTokenService loginTokenService;

    @Inject
    public UserApiController(PersistenceService persistenceService, PasswordService passwordService, LoginTokenService loginTokenService) {
        this.persistenceService = persistenceService;
        this.passwordService = passwordService;
        this.loginTokenService = loginTokenService;
    }


    @Override
    public CompletionStage<User> createUser(String mail, char[] password) {
        return persistenceService.asyncWithTransaction(false, () -> {
            User user = new User();
            user.setMail(mail);
            user.setPasswordHash(passwordService.createHash(password));
            persistenceService.persist(user);
            return user;
        });
    }

    @Override
    public CompletionStage<String> login(String mail, char[] password) {
        return persistenceService.asyncWithTransaction(true, () -> {
            Optional<User> user = persistenceService.readOne(User.class, new F.Tuple<>("mail", mail));
            if (user.isPresent() && passwordService.isPasswordCorrect(user.get().getPasswordHash(), password)) {
                return loginTokenService.create(user.get());
            } else {
                return null;
            }
        });
    }

    @Override
    public CompletionStage<Void> logout() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @WithUser
    public CompletionStage<User> getCurrentUser() {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return CompletableFuture.completedFuture(request.getAuthenticatedUser());
    }

    @Override
    @WithUser
    public CompletionStage<Void> changeUserPassword(char[] newPassword) {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return persistenceService.asyncWithTransaction(false, () -> {
            User dbUser = persistenceService.readUnique(User.class, request.getAuthenticatedUser().getId());
            dbUser.setPasswordHash(passwordService.createHash(newPassword));
            persistenceService.persist(dbUser);
            return null;
        });
    }

}
