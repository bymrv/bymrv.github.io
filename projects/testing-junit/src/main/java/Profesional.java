public abstract class Profesional implements Remunerable
{

    protected int id;
    protected String nombre;
    private float salarioBase;

    public Profesional(int pId, String pNombre, float pSalario)
    {
        super();
        this.id=pId;
        this.nombre=pNombre;
        this.salarioBase=pSalario;
    }

    public int getId() {
        return id;
    }

    public void setId(int pId) {
        this.id = pId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String pNombre) {
        this.nombre = pNombre;
    }

    public float getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(float pSalarioBase) {
        this.salarioBase = pSalarioBase;
    }
    
    public abstract double calcularRemuneracion();
}
