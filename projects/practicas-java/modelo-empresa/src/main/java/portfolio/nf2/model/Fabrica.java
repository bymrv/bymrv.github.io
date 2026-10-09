package portfolio.nf2.model;

public class Fabrica extends Edificio {
    public Fabrica(double anchura, double altura, double profundidad) {
        super(anchura, altura, profundidad);
    }

    @Override
    public String getTipo() {
        return "Fábrica";
    }

    @Override
    public String getFuncionalidad() {
        return "En este edificio se fabrican los productos.";
    }
}
