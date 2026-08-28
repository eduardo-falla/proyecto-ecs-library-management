package com.ecs.library.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor centralizado de conexiones a Base de Datos.
 * Implementa el patrón Singleton con inicialización segura (Thread-Safe).
 * Aplica SRP: Única responsabilidad de gestionar el ciclo de vida de conexiones JDBC.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String PROPERTIES_FILE = "database.properties";

    private static volatile DatabaseConnection instance;
    private final Properties properties = new Properties();

    private DatabaseConnection() {
        loadProperties();
        try {
            Class.forName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "No se encontró el driver JDBC de MySQL", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        boolean loaded = false;
        // 1. Intentar cargar desde el directorio de trabajo (raíz del proyecto)
        File externalFile = new File(PROPERTIES_FILE);
        if (externalFile.exists()) {
            try (FileInputStream fis = new FileInputStream(externalFile)) {
                properties.load(fis);
                loaded = true;
                LOGGER.info("Configuración cargada desde archivo externo: " + externalFile.getAbsolutePath());
            } catch (Exception e) {
                LOGGER.warning("Error al leer properties externo: " + e.getMessage());
            }
        }

        // 2. Si no se cargó, buscar en el Classpath
        if (!loaded) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
                if (is != null) {
                    properties.load(is);
                    loaded = true;
                    LOGGER.info("Configuración cargada desde Classpath resources.");
                }
            } catch (Exception e) {
                LOGGER.warning("Error al leer properties desde classpath: " + e.getMessage());
            }
        }

        // 3. Fallback con valores por defecto seguros
        if (!loaded) {
            LOGGER.warning("No se pudo cargar database.properties. Usando valores por defecto.");
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            properties.setProperty("db.host", "localhost");
            properties.setProperty("db.port", "3306");
            properties.setProperty("db.name", "library_management_system");
            properties.setProperty("db.username", "root");
            properties.setProperty("db.password", "");
            properties.setProperty("db.useSSL", "false");
            properties.setProperty("db.serverTimezone", "UTC");
        }
    }

    public Connection getConnection() throws SQLException {
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "3306");
        String name = properties.getProperty("db.name", "library_management_system");
        String useSSL = properties.getProperty("db.useSSL", "false");
        String timezone = properties.getProperty("db.serverTimezone", "UTC");
        String allowPublicKey = properties.getProperty("db.allowPublicKeyRetrieval", "true");

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s",
                host, port, name, useSSL, timezone, allowPublicKey);

        String user = properties.getProperty("db.username", "root");
        String pass = properties.getProperty("db.password", "");

        return DriverManager.getConnection(url, user, pass);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Prueba de conexión fallida: " + e.getMessage());
            return false;
        }
    }

    public String getDatabaseName() {
        return properties.getProperty("db.name", "library_management_system");
    }
}
