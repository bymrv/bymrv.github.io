package portfolio.nf2;

import portfolio.nf2.model.Edificio;
import portfolio.nf2.model.Producto;
import portfolio.nf2.model.Vehiculo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Empresa {
    private final List<Vehiculo> vehiculos = new ArrayList<>();
    private final List<Edificio> edificios = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();
    private final Set<Integer> identificadoresProducto = new HashSet<>();

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
    }

    public void agregarEdificio(Edificio edificio) {
        edificios.add(edificio);
    }

    public void agregarProducto(Producto producto) {
        if (!identificadoresProducto.add(producto.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con el identificador " + producto.getId() + ".");
        }
        productos.add(producto);
    }

    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public List<Edificio> getEdificios() {
        return new ArrayList<>(edificios);
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public String mostrarInfo() {
        StringBuilder informe = new StringBuilder();
        informe.append("VEHÍCULOS").append(System.lineSeparator());
        if (vehiculos.isEmpty()) {
            informe.append("Sin elementos registrados.").append(System.lineSeparator());
        } else {
            for (Vehiculo vehiculo : vehiculos) {
                informe.append("- ").append(vehiculo.mostrarInfo()).append(System.lineSeparator());
            }
        }
        informe.append(System.lineSeparator()).append("EDIFICIOS").append(System.lineSeparator());
        if (edificios.isEmpty()) {
            informe.append("Sin elementos registrados.").append(System.lineSeparator());
        } else {
            for (Edificio edificio : edificios) {
                informe.append("- ").append(edificio.mostrarInfo()).append(System.lineSeparator());
            }
        }
        informe.append(System.lineSeparator()).append("PRODUCTOS").append(System.lineSeparator());
        if (productos.isEmpty()) {
            informe.append("Sin elementos registrados.").append(System.lineSeparator());
        } else {
            for (Producto producto : productos) {
                informe.append("- ").append(producto.mostrarInfo()).append(System.lineSeparator());
            }
        }
        return informe.toString();
    }
}
