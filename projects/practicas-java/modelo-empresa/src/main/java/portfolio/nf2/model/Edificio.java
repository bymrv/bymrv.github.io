package portfolio.nf2.model;

public abstract class Edificio {
    private final double anchuraCentimetros;
    private final double alturaCentimetros;
    private final double profundidadCentimetros;

    protected Edificio(double anchura, double altura, double profundidad) {
        if (anchura <= 0 || altura <= 0 || profundidad <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser mayores que cero.");
        }
        this.anchuraCentimetros = anchura;
        this.alturaCentimetros = altura;
        this.profundidadCentimetros = profundidad;
    }

    public abstract String getTipo();

    public abstract String getFuncionalidad();

    public double getSuperficieExterior() {
        double anchura = anchuraCentimetros / 100;
        double altura = alturaCentimetros / 100;
        double profundidad = profundidadCentimetros / 100;
        return 2 * (anchura * altura + anchura * profundidad + altura * profundidad);
    }

    public double costePintura(double precioPorMetroCuadrado) {
        if (precioPorMetroCuadrado < 0) {
            throw new IllegalArgumentException("El precio por metro cuadrado no puede ser negativo.");
        }
        return getSuperficieExterior() * precioPorMetroCuadrado;
    }

    public String mostrarInfo() {
        return String.format("%s | %.0f × %.0f × %.0f cm | %s", getTipo(), anchuraCentimetros, alturaCentimetros, profundidadCentimetros, getFuncionalidad());
    }
}
