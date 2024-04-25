package me.redstoner2019.authserver.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Token {
    private String username;
    private long createStamp;
    private long maxAge;
    private String token;
    public boolean isValid(){
        return System.currentTimeMillis() < createStamp + maxAge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCreateStamp() {
        return createStamp;
    }

    public void setCreateStamp(long createStamp) {
        this.createStamp = createStamp;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public Token(String username, long createStamp, long maxAge, String password) {
        this.username = username;
        this.createStamp = createStamp;
        this.maxAge = maxAge;
        Random random = new Random(69);
        UUID uuid = new UUID(random.nextLong(),random.nextLong());
        UUID uuid2 = new UUID(random.nextLong(),random.nextLong());
        UUID uuid3 = new UUID(random.nextLong(),random.nextLong());
        UUID uuid4 = new UUID(random.nextLong(),random.nextLong());
        try {
            this.token = hashPassword(uuid.toString() + uuid2.toString() + uuid3.toString() + uuid4.toString() + username + password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Token createToken(String username){
        return new Token(username,System.currentTimeMillis(), TimeUnit.DAYS.toMillis(7),"");
    }
    public static String hashPassword(String password) throws Exception {
        Calendar calendar = Calendar.getInstance();

        int seed = Integer.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + "" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR));
        Random random = new Random(seed);
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536/8, 1024);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
}
