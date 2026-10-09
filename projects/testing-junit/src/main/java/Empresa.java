public class Empresa
{
    private String razonSocial;
    public static int numProfesionales=0;

    public Empresa(String pRazonSocial)
    {
        super();
        this.razonSocial=pRazonSocial;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String pRazonSocial) {
        this.razonSocial=pRazonSocial;
    }

    public static void main(String args[]) {
        Empresa miEmpresa=new Empresa("ACME, S.A.");
        TrabajadorCuentaAjena pedro=new TrabajadorCuentaAjena(1,"Pedro García",1650,Genero.HOMBRE,"Recursos Humanos");
        TrabajadorCuentaAjena maria=new TrabajadorCuentaAjena(2,"María Ruíz",2175,Genero.MUJER,"Dirección");
        Autonomo luisa=new Autonomo(3,"Luisa Muñoz",1000,45);
        Autonomo juan=new Autonomo(4,"Juan López",700,65);
        luisa.setHorasTrabajadas(10);
        juan.setHorasTrabajadas(30);
        
        System.out.println("RESUMEN DE PROFESIONALES DE LA EMPRESA:");
        System.out.println("---------------------------------------\n");
        
        System.out.println("TRABAJADORES POR CUENTA AJENA:\n\t"+pedro.getNombre()+" ("+pedro.getGenero()+" - "+pedro.calcularRemuneracion()+"e)\n\t"+
        maria.getNombre()+" ("+maria.getGenero()+" - "+maria.calcularRemuneracion()+"e)");
        
        System.out.println("TRABAJADORES AUTÓNOMOS:\n\t"+luisa.getNombre()+" ("+luisa.getSalarioBase()+" + ["+luisa.getPrecioHora()+"e/h x "+luisa.getHorasTrabajadas()+
        " horas] = "+luisa.calcularRemuneracion()+"e)\n\t"+
        juan.getNombre()+" ("+juan.getSalarioBase()+" + ["+juan.getPrecioHora()+"e/h x "+juan.getHorasTrabajadas()+
        " horas] = "+juan.calcularRemuneracion()+"e)\n");
        
        System.out.println("TOTAL PROFESIONALES: "+numProfesionales);
    }
}
