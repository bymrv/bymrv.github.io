package portfolio.nf2.model;

public class Diesel extends Vehiculo {
    public Diesel(String marca, double precio, double peso, String color, double longitud) {
        super(marca, precio, peso, color, longitud);
    }

    @Override
    public String getTipoCombustible() {
        return "Diésel";
    }
}
