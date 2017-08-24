package services;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import models.user.User;
import play.Environment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LoginTokenService {

    protected static final String CRYPTO_SECRET = "play.http.secret.key";
    public static final String AUTHENTICATION_TYPE = "Token";
    private static final String TOKEN_SEPARATOR = "-";
    private static final String MAC_ALGORITHM = "HmacSHA1";
    private final Mac mac;
    private final PersistenceService persistenceService;

    @Inject
    public LoginTokenService(PersistenceService persistenceService, Config configuration, Environment environment) {
        this.persistenceService = persistenceService;
        String secret = configuration.getString(LoginTokenService.CRYPTO_SECRET);
        if (secret == null) {
            throw new ConfigException.Missing(CRYPTO_SECRET);
        } else if (environment.isProd() && secret.equals("changeme")) {
            throw new ConfigException.BadValue(CRYPTO_SECRET, "ist noch immer 'changeme'");
        }
        try {
            mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(), MAC_ALGORITHM));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String create(User user) {
        return create(String.valueOf(user.getId()));
    }

    public String create(String userId) {
        return AUTHENTICATION_TYPE + " " + userId + TOKEN_SEPARATOR + calculateToken(userId);
    }

    public CompletionStage<Optional<User>> unpack(String tokenString) {
        String[] split = tokenString.replaceFirst(AUTHENTICATION_TYPE + "\\s+", "").split(TOKEN_SEPARATOR, 2);
        String userId = split[0];
        String macToken = split[1];
        if (userId.matches("\\d+") && macToken.equals(calculateToken(userId))) {
            return persistenceService.asyncWithTransaction(true, () -> persistenceService.readOne(User.class, Long.parseLong(userId)));
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    private String calculateToken(String input) {
        return new String(Base64.getEncoder().encode(mac.doFinal(input.getBytes())));
    }

}
