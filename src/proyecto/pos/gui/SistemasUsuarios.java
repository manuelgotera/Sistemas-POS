package proyecto.pos.gui;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SistemasUsuarios extends JFrame {

    private JTextField txtId;
    private JPasswordField txtPassword;
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";

    public SistemasUsuarios() {
        setTitle("Sistema de Usuarios - Restaurante");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Sistema de Usuarios");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(80, 20, 250, 30);
        add(lblTitulo);

        JLabel lblId = new JLabel("ID Usuario:");
        lblId.setBounds(50, 80, 100, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(160, 80, 170, 25);
        add(txtId);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 120, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 120, 170, 25);
        add(txtPassword);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(50, 180, 130, 35);
        add(btnRegistrar);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(200, 180, 130, 35);
        add(btnLogin);

        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnLogin.addActionListener(e -> iniciarSesion());
    }

    private void registrarUsuario() {
        String id = txtId.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }

        Map<String, String> usuarios = cargarUsuarios();

        if (usuarios.containsKey(id)) {
            JOptionPane.showMessageDialog(this, "Ese ID de usuario ya existe.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            writer.write(id + "," + password);
            writer.newLine();

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");

            txtId.setText("");
            txtPassword.setText("");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el usuario.");
        }
    }

    private void iniciarSesion() {
        String id = txtId.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }

        Map<String, String> usuarios = cargarUsuarios();

        if (usuarios.containsKey(id) && usuarios.get(id).equals(password)) {
            JOptionPane.showMessageDialog(this, "Bienvenido al sistema, usuario: " + id);

            // Aquí puedes abrir tu ventana principal del restaurante
            abrirMenuPrincipal();

        } else {
            JOptionPane.showMessageDialog(this, "ID o contraseña incorrectos.");
        }
    }

    private Map<String, String> cargarUsuarios() {
        Map<String, String> usuarios = new HashMap<>();

        File archivo = new File(ARCHIVO_USUARIOS);

        if (!archivo.exists()) {
            return usuarios;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length == 2) {
                    String id = datos[0];
                    String password = datos[1];
                    usuarios.put(id, password);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer usuarios.");
        }

        return usuarios;
    }

    private void abrirMenuPrincipal() {
        JFrame menu = new JFrame("Menú Principal - Restaurante");
        menu.setSize(400, 250);
        menu.setLocationRelativeTo(null);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setLayout(null);

        JLabel lblBienvenida = new JLabel("Bienvenido al sistema del restaurante");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        lblBienvenida.setBounds(40, 40, 330, 30);
        menu.add(lblBienvenida);

        JButton btnClientes = new JButton("Clientes");
        btnClientes.setBounds(120, 90, 150, 35);
        menu.add(btnClientes);

        JButton btnPedidos = new JButton("Pedidos");
        btnPedidos.setBounds(120, 135, 150, 35);
        menu.add(btnPedidos);

        this.dispose();
        menu.setVisible(true);
    }

    public static void main(String[] args) {
        SistemasUsuarios ventana = new SistemasUsuarios();
        ventana.setVisible(true);
    }
}