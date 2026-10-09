package portfolio.nf2.model;

public class Electrico extends Vehiculo {
    private final double potencia;

    public Electrico(String marca, double precio, double peso, String color, double longitud, double potencia) {
        super(marca, precio, peso, color, longitud);
        if (potencia <= 0) throw new IllegalArgumentException("La potencia debe ser mayor que cero.");
        this.potencia = potencia;
    }

    @Override
    public String getTipoCombustible() {
        return String.format("Eléctrico | %.1f kW", potencia);
    }
}
