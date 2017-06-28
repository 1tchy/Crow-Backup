package client.logics.connectors.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ServerAuthentication {

    @Nullable
    private String authenticationToken = null;

    Optional<String> getAuthenticationToken() {
        return Optional.ofNullable(authenticationToken);
    }

    void setAuthenticationToken(@Nonnull String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    void logout() {
        this.authenticationToken = null;
    }
}
