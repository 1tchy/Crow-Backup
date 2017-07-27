package client.logics.connectors.server.implementations;

import client.logics.connectors.server.AbstractServerConnector;
import client.logics.connectors.server.ServerAuthentication;
import com.google.inject.Singleton;
import interfaces.UserServerInterface;
import models.user.User;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletionStage;


@Singleton
public class UserServerConnector extends AbstractServerConnector implements UserServerInterface {

    @Inject
    public UserServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        super(ws, serverAuthentication);
    }

    @Override
    public CompletionStage<User> createUser(String mail, char[] password) {
        return apiCall(User.class, mail, password);
    }

    /**
     * @return ob das Login erfolgreich war
     */
    @SuppressWarnings("deprecation")
    public CompletionStage<Boolean> loginAndRemember(String mail, char[] password) {
        return login(mail, password).thenApply(Objects::nonNull);
    }

    @Override
    @Deprecated // existiert nur als Schnittstelle zwischen Client und Server, besser loginAndRemember() verwenden!
    @SuppressWarnings("DeprecatedIsStillUsed")
    public CompletionStage<String> login(String mail, char[] password) {
        return apiCall(String.class, mail, password).thenApply(token -> {
            doLogin(token);
            return token;
        });
    }

    @Override
    public CompletionStage<Void> logout() {
        CompletionStage<Void> call = apiCall(Void.class);
        doLogout();
        return call;
    }

    @Override
    public CompletionStage<User> getCurrentUser() {
        return apiCall(User.class);
    }

    @Override
    public CompletionStage<Void> changeUserPassword(char[] newPassword) {
        return apiCall(Void.class, (Object) newPassword);
    }

}
