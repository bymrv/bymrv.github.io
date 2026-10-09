public class Autonomo extends Profesional
{

    private float precioHora;
    private float horasTrabajadas=0;

    public Autonomo(int pId, String pNombre, float pSalario, float pPrecioHora)
    {
        super(pId,pNombre,pSalario);
        this.precioHora=pPrecioHora;
        Empresa.numProfesionales++;
    }

    public float getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(float pPrecioHora) {
        this.precioHora = pPrecioHora;
    }

    public float getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(float pHorasTrabajadas) {
        this.horasTrabajadas = pHorasTrabajadas;
    }

    public double calcularRemuneracion() {
        return getSalarioBase()+(getPrecioHora()*getHorasTrabajadas());
    }
}
