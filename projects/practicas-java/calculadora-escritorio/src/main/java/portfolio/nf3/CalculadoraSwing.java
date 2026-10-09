package portfolio.nf3;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CalculadoraSwing {
    private final JTextField pantalla = new JTextField("0");
    private final JTextArea historial = new JTextArea();
    private final Path ficheroHistorial = Paths.get("data", "historico.txt");
    private double primerNumero;
    private String operador;
    private boolean iniciarNumero = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculadoraSwing().mostrarVentana());
    }

    private void mostrarVentana() {
        JFrame ventana = new JFrame("Calculadora");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setMinimumSize(new java.awt.Dimension(390, 480));
        ventana.setLayout(new BorderLayout(14, 14));

        JPanel contenido = new JPanel(new BorderLayout(10, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 8));
        pantalla.setEditable(false);
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT);
        pantalla.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        pantalla.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        contenido.add(pantalla, BorderLayout.NORTH);

        JPanel teclado = new JPanel(new GridLayout(5, 4, 8, 8));
        String[] teclas = {"C", "PAR", "/", "*", "7", "8", "9", "-", "4", "5", "6", "+", "1", "2", "3", "=", "0", ",", "⌫", " "};
        for (int i = 0; i < teclas.length; i++) {
            String texto = teclas[i];
            JButton boton = new JButton(texto);
            boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
            boton.setEnabled(!texto.trim().isEmpty());
            boton.addActionListener(evento -> pulsar(texto));
            teclado.add(boton);
        }
        contenido.add(teclado, BorderLayout.CENTER);
        ventana.add(contenido, BorderLayout.CENTER);

        JPanel panelHistorial = new JPanel(new BorderLayout(6, 8));
        panelHistorial.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 16));
        panelHistorial.setPreferredSize(new java.awt.Dimension(230, 0));
        panelHistorial.add(new JLabel("Historial reciente"), BorderLayout.NORTH);
        historial.setEditable(false);
        historial.setLineWrap(true);
        historial.setWrapStyleWord(true);
        historial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panelHistorial.add(new JScrollPane(historial), BorderLayout.CENTER);
        ventana.add(panelHistorial, BorderLayout.EAST);

        cargarHistorial();
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void pulsar(String texto) {
        if (texto.matches("[0-9]")) {
            introducirDigito(texto);
            return;
        }
        switch (texto) {
            case ",":
                introducirSeparador();
                break;
            case "C":
                limpiar();
                break;
            case "⌫":
                borrarUltimo();
                break;
            case "PAR":
                comprobarParidad();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                seleccionarOperador(texto);
                break;
            case "=":
                calcular();
                break;
            default:
                break;
        }
    }

    private void introducirDigito(String digito) {
        if (iniciarNumero || pantalla.getText().equals("0")) {
            pantalla.setText(digito);
            iniciarNumero = false;
        } else if (pantalla.getText().length() < 18) {
            pantalla.setText(pantalla.getText() + digito);
        }
    }

    private void introducirSeparador() {
        if (iniciarNumero) {
            pantalla.setText("0,");
            iniciarNumero = false;
        } else if (!pantalla.getText().contains(",")) {
            pantalla.setText(pantalla.getText() + ",");
        }
    }

    private void seleccionarOperador(String nuevoOperador) {
        try {
            double valor = leerPantalla();
            if (operador != null && !iniciarNumero) {
                calcular();
                valor = leerPantalla();
            }
            primerNumero = valor;
            operador = nuevoOperador;
            iniciarNumero = true;
        } catch (NumberFormatException e) {
            mostrarError("Introduce un número válido.");
        }
    }

    private void calcular() {
        if (operador == null) {
            return;
        }
        try {
            double segundoNumero = leerPantalla();
            double resultado;
            if (operador.equals("+")) {
                resultado = primerNumero + segundoNumero;
            } else if (operador.equals("-")) {
                resultado = primerNumero - segundoNumero;
            } else if (operador.equals("*")) {
                resultado = primerNumero * segundoNumero;
            } else if (operador.equals("/")) {
                if (segundoNumero == 0) throw new ArithmeticException("No se puede dividir entre cero.");
                resultado = primerNumero / segundoNumero;
            } else {
                throw new IllegalStateException("Operación no reconocida.");
            }
            String linea = formatear(primerNumero) + " " + operador + " " + formatear(segundoNumero) + " = " + formatear(resultado);
            pantalla.setText(formatear(resultado));
            guardar(linea);
            primerNumero = resultado;
            operador = null;
            iniciarNumero = true;
        } catch (ArithmeticException | NumberFormatException e) {
            mostrarError(e.getMessage() == null ? "No se pudo realizar la operación." : e.getMessage());
            operador = null;
            iniciarNumero = true;
        }
    }

    private void comprobarParidad() {
        try {
            double valor = leerPantalla();
            if (valor != Math.floor(valor) || valor > Integer.MAX_VALUE || valor < Integer.MIN_VALUE) {
                mostrarError("La comprobación de paridad requiere un número entero.");
                return;
            }
            String tipo = ((long) valor % 2 == 0) ? "Par" : "Impar";
            guardar(formatear(valor) + " es " + tipo.toLowerCase());
            pantalla.setText(tipo);
            operador = null;
            iniciarNumero = true;
        } catch (NumberFormatException | ArithmeticException e) {
            mostrarError("Introduce un entero válido.");
        }
    }

    private void borrarUltimo() {
        String texto = pantalla.getText();
        if (texto.equals("Error") || texto.equals("Par") || texto.equals("Impar") || texto.length() <= 1) {
            pantalla.setText("0");
            iniciarNumero = true;
        } else {
            pantalla.setText(texto.substring(0, texto.length() - 1));
        }
    }

    private double leerPantalla() {
        return Double.parseDouble(pantalla.getText().replace(',', '.'));
    }

    private String formatear(double numero) {
        DecimalFormat formato = new DecimalFormat("0.##########", DecimalFormatSymbols.getInstance(new Locale("es", "ES")));
        return formato.format(numero);
    }

    private void limpiar() {
        pantalla.setText("0");
        operador = null;
        iniciarNumero = true;
    }

    private void mostrarError(String mensaje) {
        pantalla.setText("Error");
        javax.swing.JOptionPane.showMessageDialog(null, mensaje, "Calculadora", javax.swing.JOptionPane.WARNING_MESSAGE);
        pantalla.setText("0");
        operador = null;
        iniciarNumero = true;
    }

    private void guardar(String linea) {
        try {
            Files.createDirectories(ficheroHistorial.getParent());
            Files.write(ficheroHistorial, java.util.Collections.singletonList(linea), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            historial.append(linea + System.lineSeparator());
            String[] lineas = historial.getText().split("\\R");
            if (lineas.length > 50) {
                String[] recientes = Arrays.copyOfRange(lineas, lineas.length - 50, lineas.length);
                historial.setText(String.join(System.lineSeparator(), recientes));
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "No se pudo guardar el historial.", "Calculadora", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarHistorial() {
        try {
            if (Files.exists(ficheroHistorial)) {
                List<String> lineas = Files.readAllLines(ficheroHistorial, StandardCharsets.UTF_8);
                int inicio = Math.max(0, lineas.size() - 50);
                historial.setText(String.join(System.lineSeparator(), lineas.subList(inicio, lineas.size())));
            }
        } catch (Exception e) {
            historial.setText("No se pudo cargar el historial.");
        }
    }
}
