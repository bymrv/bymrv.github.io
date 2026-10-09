package portfolio.nf4;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TiendaApp {
    private final Scanner entrada = new Scanner(System.in);
    private final ProductoRepository productos;

    private TiendaApp(Connection conexion) {
        productos = new ProductoRepository(conexion);
    }

    public static void main(String[] args) {
        try (Connection conexion = Conexion.abrir()) {
            System.out.println("Conexión con MySQL establecida.");
            new TiendaApp(conexion).iniciar();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar con MySQL: " + e.getMessage());
            System.out.println("Revisa que MySQL esté iniciado, que exista la base tienda y que DB_USER/DB_PASSWORD sean correctos.");
        }
    }

    private void iniciar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            switch (leerEntero("Elige una opción: ")) {
                case 1:
                    crear();
                    break;
                case 2:
                    listar();
                    break;
                case 3:
                    modificar();
                    break;
                case 4:
                    borrar();
                    break;
                case 0:
                    System.out.println("Fin del programa.");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            System.out.println();
        }
    }

    private void mostrarMenu() {
        System.out.println("***********************");
        System.out.println("** GESTOR DE PRODUCTOS **");
        System.out.println("***********************");
        System.out.println("1) Crear un producto");
        System.out.println("2) Listar todos los productos");
        System.out.println("3) Modificar un producto");
        System.out.println("4) Borrar un producto");
        System.out.println("0) Salir del programa");
    }

    private void crear() {
        String nombre = leerNombre("Nombre del producto: ");
        BigDecimal precio = leerPrecio("Precio: ");
        int cantidad = leerCantidad("Cantidad: ");
        try {
            int id = productos.crear(nombre, precio, cantidad);
            System.out.println("Producto creado con el identificador " + id + ".");
        } catch (SQLException e) {
            mostrarError("crear el producto", e);
        }
    }

    private void listar() {
        try {
            List<Producto> lista = productos.listar();
            if (lista.isEmpty()) {
                System.out.println("La lista está vacía.");
                return;
            }
            for (Producto producto : lista) {
                System.out.println(producto.mostrarInfo());
            }
        } catch (SQLException e) {
            mostrarError("listar los productos", e);
        }
    }

    private void modificar() {
        try {
            List<Producto> lista = productos.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay productos para modificar.");
                return;
            }
            for (Producto producto : lista) {
                System.out.println(producto.mostrarInfo());
            }
            int id = leerEntero("Identificador del producto: ");
            System.out.println("1) Nombre");
            System.out.println("2) Precio");
            System.out.println("3) Cantidad");
            boolean actualizado;
            switch (leerEntero("Campo que quieres modificar: ")) {
                case 1:
                    actualizado = productos.actualizarNombre(id, leerNombre("Nuevo nombre: "));
                    break;
                case 2:
                    actualizado = productos.actualizarPrecio(id, leerPrecio("Nuevo precio: "));
                    break;
                case 3:
                    actualizado = productos.actualizarCantidad(id, leerCantidad("Nueva cantidad: "));
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }
            System.out.println(actualizado ? "Producto modificado." : "No se encontró ese identificador.");
        } catch (SQLException e) {
            mostrarError("modificar el producto", e);
        }
    }

    private void borrar() {
        try {
            List<Producto> lista = productos.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay productos para borrar.");
                return;
            }
            for (Producto producto : lista) {
                System.out.println(producto.mostrarInfo());
            }
            int id = leerEntero("Identificador del producto que quieres borrar: ");
            System.out.println(productos.borrar(id) ? "Producto borrado." : "No se encontró ese identificador.");
        } catch (SQLException e) {
            mostrarError("borrar el producto", e);
        }
    }

    private String leerNombre(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String nombre = entrada.nextLine().trim();
            if (!nombre.isEmpty() && nombre.length() <= 120) return nombre;
            System.out.println("El nombre debe contener entre 1 y 120 caracteres.");
        }
    }

    private BigDecimal leerPrecio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                BigDecimal precio = new BigDecimal(entrada.nextLine().trim().replace(',', '.'));
                if (precio.signum() >= 0 && precio.scale() <= 2 && precio.precision() - precio.scale() <= 8) {
                    return precio.setScale(2);
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Introduce un precio válido con hasta dos decimales.");
        }
    }

    private int leerCantidad(String mensaje) {
        while (true) {
            int cantidad = leerEntero(mensaje);
            if (cantidad >= 0) return cantidad;
            System.out.println("La cantidad no puede ser negativa.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
    }

    private void mostrarError(String accion, SQLException e) {
        System.out.println("No se pudo " + accion + ": " + e.getMessage());
    }
}
