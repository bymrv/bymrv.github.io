package portfolio.nf4;

import java.math.BigDecimal;

public class Producto {
    private final int id;
    private final String nombre;
    private final BigDecimal precio;
    private final int cantidad;

    public Producto(int id, String nombre, BigDecimal precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String mostrarInfo() {
        return String.format("%d | %s | %s € | %d unidades", id, nombre, precio.toPlainString(), cantidad);
    }
}
