package portfolio.nf2.model;

public class Gasolina extends Vehiculo {
    private final double contaminacion;
    private final double tamanioDeposito;

    public Gasolina(String marca, double precio, double peso, String color, double longitud, double contaminacion, double tamanioDeposito) {
        super(marca, precio, peso, color, longitud);
        if (contaminacion < 0 || tamanioDeposito <= 0) {
            throw new IllegalArgumentException("Datos de gasolina no válidos.");
        }
        this.contaminacion = contaminacion;
        this.tamanioDeposito = tamanioDeposito;
    }

    @Override
    public String getTipoCombustible() {
        return String.format("Gasolina | emisiones %.1f | depósito %.1f l", contaminacion, tamanioDeposito);
    }
}
