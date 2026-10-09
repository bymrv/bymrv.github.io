package portfolio.nf2.model;

public abstract class Producto {
    private final int id;
    private final String nombre;
    private final double anchura;
    private final double profundidad;
    private final double altura;

    protected Producto(int id, String nombre, double anchura, double profundidad, double altura) {
        if (id <= 0 || nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador y el nombre son obligatorios.");
        }
        if (anchura <= 0 || profundidad <= 0 || altura <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser mayores que cero.");
        }
        this.id = id;
        this.nombre = nombre;
        this.anchura = anchura;
        this.profundidad = profundidad;
        this.altura = altura;
    }

    public abstract String getTipo();

    public int getId() {
        return id;
    }

    public String mostrarInfo() {
        return String.format("#%d | %s | %s | %.2f × %.2f × %.2f m", id, nombre, getTipo(), anchura, profundidad, altura);
    }
}
