package cn.ieasy.framework.common.web.utils.encode;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCryptPasswordEncoder implements PasswordEncoder {
    private final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int strength;

    private final SecureRandom random;

    public BCryptPasswordEncoder() {
        this(-1);
    }

    /**
     * @param strength the log rounds to use
     */
    public BCryptPasswordEncoder(final int strength) {
        this(strength, null);
    }

    /**
     * @param strength the log rounds to use
     * @param random the secure random instance to use
     */
    public BCryptPasswordEncoder(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    public String encode(final CharSequence rawPassword) {
        String salt;
        if (this.strength > 0) {
            if (this.random != null) {
                salt = BCrypt.gensalt(this.strength, this.random);
            }
            else {
                salt = BCrypt.gensalt(this.strength);
            }
        }
        else {
            salt = BCrypt.gensalt();
        }
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        if ((encodedPassword == null) || (encodedPassword.length() == 0)) {
            this.logger.warn("Empty encoded password");
            return false;
        }

        if (!this.BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            this.logger.warn("Encoded password does not look like BCrypt");
            return false;
        }

        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}
