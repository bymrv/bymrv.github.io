import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrabajadorCuentaAjenaTest
{
    private TrabajadorCuentaAjena maria;

    public TrabajadorCuentaAjenaTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
        maria = new TrabajadorCuentaAjena(1, "María", 2400.0f, Genero.MUJER, "Dpto.Ventas");
    }

    @AfterEach
    public void tearDown()
    {
    }

    @Test
    public void probarCalculoRemuneracion()
    {
        float nuevoSalarioBase = 2000.0f / 3.0f; 
        maria.setSalarioBase(nuevoSalarioBase);
        double valorEsperado = 786.67;
        double delta = 0.1;
        
        assertEquals(valorEsperado, maria.calcularRemuneracion(), delta, 
                 "La remuneración calculada para TrabajadorCuentaAjena (Maria) no es la esperada.");
    }
}
