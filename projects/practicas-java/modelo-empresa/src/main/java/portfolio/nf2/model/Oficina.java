package portfolio.nf2.model;

public class Oficina extends Edificio {
    public Oficina(double anchura, double altura, double profundidad) {
        super(anchura, altura, profundidad);
    }

    @Override
    public String getTipo() {
        return "Oficina";
    }

    @Override
    public String getFuncionalidad() {
        return "En este edificio se gestiona y etiqueta el producto.";
    }
}
