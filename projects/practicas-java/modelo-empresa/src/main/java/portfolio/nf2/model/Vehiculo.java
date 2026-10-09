package portfolio.nf2.model;

public abstract class Vehiculo {
    private final String marca;
    private final double precio;
    private final double peso;
    private final String color;
    private final double longitud;

    protected Vehiculo(String marca, double precio, double peso, String color, double longitud) {
        if (marca == null || marca.trim().isEmpty() || color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca y el color son obligatorios.");
        }
        if (precio < 0 || peso <= 0 || longitud <= 0) {
            throw new IllegalArgumentException("Precio, peso o longitud no válidos.");
        }
        this.marca = marca;
        this.precio = precio;
        this.peso = peso;
        this.color = color;
        this.longitud = longitud;
    }

    public abstract String getTipoCombustible();

    public String mostrarInfo() {
        return String.format("%s | %s | %.2f € | %.0f kg | %s | %.2f m", marca, getTipoCombustible(), precio, peso, color, longitud);
    }
}
