import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AutonomoTest
{
    private Autonomo juan;

    public AutonomoTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
        juan = new Autonomo(1, "Juan", 1800.0f, 25.5f);
        juan.setPrecioHora(25.5f);
        juan.setHorasTrabajadas(0.0f);
    }

    @AfterEach
    public void tearDown()
    {
    }
    @Test
    public void probarCalculoRemuneracion()
    { 
        juan.setPrecioHora(35.0f);
        juan.setHorasTrabajadas(60.0f);
        double valorEsperado = 3900;
        double delta = 0.1f;
        
        assertEquals(valorEsperado, juan.calcularRemuneracion(), delta, 
                     "La remuneración calculada para Autonomo (Juan) no es la esperada.");
    }   
}
