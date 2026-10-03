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
            LOGGER.info("Base de datos no detectada. Intentando auto-creación en MySQL...");
            if (initializeDatabaseIfMissing()) {
                try (Connection retryConn = getConnection()) {
                    return retryConn != null && !retryConn.isClosed();
                } catch (SQLException ex) {
                    LOGGER.warning("Reintento fallido: " + ex.getMessage());
                }
            }
            return false;
        }
    }

    public synchronized boolean initializeDatabaseIfMissing() {
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "3306");
        String name = properties.getProperty("db.name", "library_management_system");
        String useSSL = properties.getProperty("db.useSSL", "false");
        String timezone = properties.getProperty("db.serverTimezone", "UTC");
        String allowPublicKey = properties.getProperty("db.allowPublicKeyRetrieval", "true");
        String user = properties.getProperty("db.username", "root");
        String pass = properties.getProperty("db.password", "");

        String serverUrl = String.format("jdbc:mysql://%s:%s/?useSSL=%s&serverTimezone=%s&allowPublicKeyRetrieval=%s",
                host, port, useSSL, timezone, allowPublicKey);

        try (Connection serverConn = DriverManager.getConnection(serverUrl, user, pass);
             java.sql.Statement stmt = serverConn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            LOGGER.info("Base de datos '" + name + "' verificada o creada con éxito.");

            // Ejecutar script SQL de esquema y tablas si existen
            File sqlFile = new File("sql/library_v2_schema.sql");
            if (!sqlFile.exists()) {
                sqlFile = new File("library-management-system-refactored/sql/library_v2_schema.sql");
            }
            if (sqlFile.exists()) {
                executeSqlScript(sqlFile);
            }
            return true;
        } catch (SQLException e) {
            LOGGER.warning("No se pudo auto-inicializar la BD (MySQL podría estar apagado): " + e.getMessage());
            return false;
        }
    }

    private void executeSqlScript(File sqlFile) {
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(sqlFile, java.nio.charset.StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.startsWith("/*") || line.isEmpty()) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString().trim();
                    sb.setLength(0);
                    // Omitir comandos que cambian de base de datos a nivel global si ya estamos conectados
                    if (!sql.toUpperCase().startsWith("DROP DATABASE") && !sql.toUpperCase().startsWith("CREATE DATABASE") && !sql.toUpperCase().startsWith("USE ")) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException ex) {
                            LOGGER.fine("Comando SQL omitido o ya existente: " + ex.getMessage());
                        }
                    }
                }
            }
            LOGGER.info("Esquema relacional y datos semilla poblados exitosamente.");
        } catch (Exception e) {
            LOGGER.warning("Advertencia al ejecutar script SQL inicial: " + e.getMessage());
        }
    }

    public String getDatabaseName() {
        return properties.getProperty("db.name", "library_management_system");
    }
}
