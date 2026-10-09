package portfolio.nf4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {
    private final Connection conexion;

    public ProductoRepository(Connection conexion) {
        this.conexion = conexion;
    }

    public int crear(String nombre, BigDecimal precio, int cantidad) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement consulta = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            consulta.setString(1, nombre);
            consulta.setBigDecimal(2, precio);
            consulta.setInt(3, cantidad);
            consulta.executeUpdate();
            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) return claves.getInt(1);
            }
        }
        throw new SQLException("MySQL no devolvió el identificador del producto.");
    }

    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, cantidad FROM productos ORDER BY id";
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultados = consulta.executeQuery()) {
            while (resultados.next()) {
                productos.add(new Producto(resultados.getInt("id"), resultados.getString("nombre"),
                        resultados.getBigDecimal("precio"), resultados.getInt("cantidad")));
            }
        }
        return productos;
    }

    public boolean actualizarNombre(int id, String nombre) throws SQLException {
        return actualizar("UPDATE productos SET nombre = ? WHERE id = ?", nombre, id);
    }

    public boolean actualizarPrecio(int id, BigDecimal precio) throws SQLException {
        String sql = "UPDATE productos SET precio = ? WHERE id = ?";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setBigDecimal(1, precio);
            consulta.setInt(2, id);
            return consulta.executeUpdate() > 0;
        }
    }

    public boolean actualizarCantidad(int id, int cantidad) throws SQLException {
        String sql = "UPDATE productos SET cantidad = ? WHERE id = ?";
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, cantidad);
            consulta.setInt(2, id);
            return consulta.executeUpdate() > 0;
        }
    }

    public boolean borrar(int id) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            consulta.setInt(1, id);
            return consulta.executeUpdate() > 0;
        }
    }

    private boolean actualizar(String sql, String nombre, int id) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, nombre);
            consulta.setInt(2, id);
            return consulta.executeUpdate() > 0;
        }
    }
}
