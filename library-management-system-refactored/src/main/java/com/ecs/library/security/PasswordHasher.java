package com.ecs.library.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utilidad de seguridad criptográfica para almacenamiento y verificación de contraseñas.
 * Corrige la mala práctica crítica de contraseñas en texto plano del sistema legado.
 * Aplica SHA-256 con salt dinámico y generación segura.
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return bytesToHex(salt);
    }

    public static String hash(String plainPassword, String salt) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String combined = plainPassword + (salt != null ? salt : "");
            byte[] hashedBytes = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al instanciar algoritmo de cifrado: " + ALGORITHM, e);
        }
    }

    public static boolean verify(String plainPassword, String storedHash, String salt) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }

        // 1. Verificación directa contra hash seguro
        String computedHash = hash(plainPassword, salt);
        if (computedHash.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // 2. Compatibilidad retroactiva durante proceso de migración (si la BD aún tiene texto plano)
        return plainPassword.equals(storedHash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
