package portfolio.nf2;

import portfolio.nf2.model.Almacen;
import portfolio.nf2.model.Diesel;
import portfolio.nf2.model.Electrico;
import portfolio.nf2.model.Fabrica;
import portfolio.nf2.model.Oficina;
import portfolio.nf2.model.Puerta;
import portfolio.nf2.model.Silla;
import portfolio.nf2.model.Gasolina;

public class DemoEmpresa {
    public static void main(String[] args) {
        Empresa empresa = new Empresa();
        empresa.agregarVehiculo(new Electrico("Seat Altea", 20000, 3000, "Rojo", 4.0, 80));
        empresa.agregarVehiculo(new Diesel("Renault Kadjar", 28000, 4000, "Azul", 3.5));
        empresa.agregarVehiculo(new Gasolina("Toyota Corolla", 24500, 1450, "Gris", 4.4, 2.1, 50));

        Fabrica fabrica = new Fabrica(400, 400, 500);
        empresa.agregarEdificio(fabrica);
        empresa.agregarEdificio(new Oficina(600, 400, 700));
        empresa.agregarEdificio(new Almacen(600, 400, 700));

        empresa.agregarProducto(new Silla(1, "Silla N20", 1, 0.5, 0.5));
        empresa.agregarProducto(new Silla(2, "Silla M100", 1.5, 0.5, 1.5));
        empresa.agregarProducto(new Puerta(3, "Puerta P9", 1.5, 0.25, 2.25));
        empresa.agregarProducto(new Puerta(4, "Puerta F50", 1.25, 0.25, 1.25));

        System.out.println(empresa.mostrarInfo());
        System.out.printf("Pintar la fábrica a 30,00 €/m²: %.2f €%n", fabrica.costePintura(30));
    }
}
