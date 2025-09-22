package model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UserSession {

    private int userId;
    private String username;

    public UserSession(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}


// public static String hash(String plain) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] salt = new byte[16];
//            new SecureRandom().nextBytes(salt);
//            md.update(salt);
//            byte[] digest = md.digest(plain.getBytes("UTF-8"));
//            StringBuilder sb = new StringBuilder();
//            for (byte b : salt) sb.append(String.format("%02x", b));
//            sb.append(":");
//            for (byte b : digest) sb.append(String.format("%02x", b));
//            return sb.toString();
//        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
//            return plain;
//        }
//    }
