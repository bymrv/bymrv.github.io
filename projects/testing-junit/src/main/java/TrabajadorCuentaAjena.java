public class TrabajadorCuentaAjena extends Profesional
{

    private Genero genero;
    private String departamento;

    public TrabajadorCuentaAjena(int pId, String pNombre, float pSalario, Genero pGenero, String pDepartamento)
    {
        super(pId,pNombre,pSalario);
        this.genero=pGenero;
        this.departamento=pDepartamento;
        Empresa.numProfesionales++;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String pDepartamento) {
        this.departamento = pDepartamento;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero pGenero) {
        this.genero = pGenero;
    }

    public double calcularRemuneracion() 
    {
        return getSalarioBase() * 1.18f;
    }
}
