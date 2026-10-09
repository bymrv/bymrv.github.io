package portfolio.nf2.model;

public class Puerta extends Producto {
    public Puerta(int id, String nombre, double anchura, double profundidad, double altura) {
        super(id, nombre, anchura, profundidad, altura);
    }

    @Override
    public String getTipo() {
        return "Puerta";
    }
}
