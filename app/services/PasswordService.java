package services;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordService {

    private final Argon2 argon2;

    public PasswordService() {
        argon2 = Argon2Factory.create();
    }

    public boolean isPasswordCorrect(String referenceHash, char[] password) {
        try {
            return argon2.verify(referenceHash, password);
        } finally {
            argon2.wipeArray(password); // Wipe confidential data
        }

    }

    public String createHash(char[] password) {
        try {
            return argon2.hash(4, 65536, Runtime.getRuntime().availableProcessors(), password);
        } finally {
            argon2.wipeArray(password); // Wipe confidential data
        }
    }
}
