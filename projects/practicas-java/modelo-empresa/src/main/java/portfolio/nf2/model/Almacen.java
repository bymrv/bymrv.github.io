package portfolio.nf2.model;

public class Almacen extends Edificio {
    public Almacen(double anchura, double altura, double profundidad) {
        super(anchura, altura, profundidad);
    }

    @Override
    public String getTipo() {
        return "Almacén";
    }

    @Override
    public String getFuncionalidad() {
        return "Aquí se guarda el producto antes de venderlo.";
    }
}
