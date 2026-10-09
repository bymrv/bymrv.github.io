package portfolio.nf2.model;

public class Silla extends Producto {
    public Silla(int id, String nombre, double anchura, double profundidad, double altura) {
        super(id, nombre, anchura, profundidad, altura);
    }

    @Override
    public String getTipo() {
        return "Silla";
    }
}
