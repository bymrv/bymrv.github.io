package portfolio.nf4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {
    private Conexion() {
    }

    public static Connection abrir() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/tienda");
        String usuario = System.getenv().getOrDefault("DB_USER", "root");
        String contrasena = System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(url, usuario, contrasena);
    }
}
