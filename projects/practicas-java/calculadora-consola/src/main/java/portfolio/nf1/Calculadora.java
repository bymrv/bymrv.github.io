package portfolio.nf1;

import java.util.Scanner;

public class Calculadora {
    private static final int MAX_FACTORIAL = 20;

    public static double suma(double primero, double segundo) {
        return primero + segundo;
    }

    public static double resta(double primero, double segundo) {
        return primero - segundo;
    }

    public static double multiplicacion(double primero, double segundo) {
        return primero * segundo;
    }

    public static double division(double primero, double segundo) {
        return primero / segundo;
    }

    public static double mayor(double primero, double segundo, double tercero) {
        return Math.max(primero, Math.max(segundo, tercero));
    }

    public static long factorial(int numero) {
        if (numero < 0 || numero > MAX_FACTORIAL) {
            throw new IllegalArgumentException("El valor debe estar entre 0 y " + MAX_FACTORIAL + ".");
        }
        long resultado = 1;
        for (int i = 2; i <= numero; i++) {
            resultado *= i;
        }
        return resultado;
    }

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero(entrada, "Elige una opción: ");

            switch (opcion) {
                case 1:
                    double sumando1 = leerNumero(entrada, "Introduce el primer sumando: ");
                    double sumando2 = leerNumero(entrada, "Introduce el segundo sumando: ");
                    System.out.println("Resultado: " + suma(sumando1, sumando2));
                    break;
                case 2:
                    double minuendo = leerNumero(entrada, "Introduce el primer número: ");
                    double sustraendo = leerNumero(entrada, "Introduce el segundo número: ");
                    System.out.println("Resultado: " + resta(minuendo, sustraendo));
                    break;
                case 3:
                    double factor1 = leerNumero(entrada, "Introduce el primer número: ");
                    double factor2 = leerNumero(entrada, "Introduce el segundo número: ");
                    System.out.println("Resultado: " + multiplicacion(factor1, factor2));
                    break;
                case 4:
                    double dividendo = leerNumero(entrada, "Introduce el dividendo: ");
                    double divisor = leerNumero(entrada, "Introduce el divisor: ");
                    if (divisor == 0) {
                        System.out.println("No se puede dividir entre cero.");
                    } else {
                        System.out.println("Resultado: " + division(dividendo, divisor));
                    }
                    break;
                case 5:
                    double numero1 = leerNumero(entrada, "Introduce el primer número: ");
                    double numero2 = leerNumero(entrada, "Introduce el segundo número: ");
                    double numero3 = leerNumero(entrada, "Introduce el tercer número: ");
                    System.out.println("El número mayor es: " + mayor(numero1, numero2, numero3));
                    break;
                case 6:
                    int numero = leerEntero(entrada, "Introduce un entero entre 0 y " + MAX_FACTORIAL + ": ");
                    if (numero < 0 || numero > MAX_FACTORIAL) {
                        System.out.println("El valor debe estar entre 0 y " + MAX_FACTORIAL + ".");
                    } else {
                        System.out.println(numero + "! = " + factorial(numero));
                    }
                    break;
                case 0:
                    System.out.println("El programa ha finalizado.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

            if (opcion != 0) {
                System.out.println();
            }
        }

        entrada.close();
    }

    private static void mostrarMenu() {
        System.out.println("*****************************************");
        System.out.println("***** CALCULADORA DE CALCULATOR S.A *****");
        System.out.println("*****************************************");
        System.out.println("1. Sumar");
        System.out.println("2. Restar");
        System.out.println("3. Multiplicar");
        System.out.println("4. Dividir");
        System.out.println("5. Número mayor de tres números");
        System.out.println("6. Factorial");
        System.out.println("0. Salir");
    }

    private static double leerNumero(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim().replace(',', '.');
            try {
                double numero = Double.parseDouble(texto);
                if (Double.isFinite(numero)) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
                continue;
            }
            System.out.println("Introduce un número válido.");
        }
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
    }
}
