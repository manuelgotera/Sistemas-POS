package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ClientesFrame extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnAgregar, btnEliminar, btnGuardar, btnEditar;

    private int filaEditando = -1;

    private static final Color AZUL = new Color(26,35,126);

    public ClientesFrame() {
        setTitle("Gestión de Clientes");
        setSize(1100, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,245,245));

        JPanel top = new JPanel();
        top.setBackground(AZUL);
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel titulo = new JLabel("CLIENTES");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        top.add(titulo);

        modelo = new DefaultTableModel(
                new String[]{
                        "Nombre","Apellido","DNI","Teléfono","Email",
                        "TipoCliente","Puntos","Dirección","Fecha"
                },0
        ){
            public boolean isCellEditable(int row, int column){
                return row == filaEditando;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(25);

        tabla.getTableHeader().setBackground(AZUL);
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.setSelectionBackground(new Color(40,53,147));
        tabla.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        TableColumn colTipo = tabla.getColumnModel().getColumn(5);
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{
                "NATURAL","EMPRESA"
        });
        colTipo.setCellEditor(new DefaultCellEditor(comboTipo));

        JPanel botones = new JPanel();
        botones.setBackground(new Color(245,245,245));

        btnAgregar = crearBoton("Agregar");
        btnEditar = crearBoton("Editar");
        btnGuardar = crearBoton("Guardar");
        btnEliminar = crearBoton("Eliminar");

        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(botones, BorderLayout.SOUTH);

        add(root);

        btnAgregar.addActionListener(e -> modelo.addRow(
                new Object[]{"","","","","","NATURAL","0","",""}
        ));

        btnEliminar.addActionListener(e -> eliminarFila());
        btnEditar.addActionListener(e -> activarEdicion());
        btnGuardar.addActionListener(e -> guardar());
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(AZUL);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40,53,147));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(AZUL);
            }
        });

        return btn;
    }

    private void eliminarFila() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila");
            return;
        }
        modelo.removeRow(fila);
    }

    private void activarEdicion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila");
            return;
        }
        filaEditando = fila;
        modelo.fireTableDataChanged();
    }

    private void guardar() {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < modelo.getRowCount(); i++) {

            String nombre = (String) modelo.getValueAt(i,0);
            String apellido = (String) modelo.getValueAt(i,1);
            String dni = (String) modelo.getValueAt(i,2);
            String telefono = (String) modelo.getValueAt(i,3);
            String email = (String) modelo.getValueAt(i,4);
            String puntos = (String) modelo.getValueAt(i,6);
            String fecha = (String) modelo.getValueAt(i,8);

            if (nombre == null || nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,"Nombre vacío fila " + (i+1));
                return;
            }

            if (apellido == null || apellido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,"Apellido vacío fila " + (i+1));
                return;
            }

            if (dni == null || !dni.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this,"DNI inválido fila " + (i+1));
                return;
            }

            if (telefono == null || telefono.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,"Teléfono vacío fila " + (i+1));
                return;
            }

            if (email == null || !email.contains("@")) {
                JOptionPane.showMessageDialog(this,"Email inválido fila " + (i+1));
                return;
            }

            try {
                Integer.parseInt(puntos);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Puntos debe ser número fila " + (i+1));
                return;
            }

            try {
                formato.parse(fecha);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Fecha inválida (dd/MM/yyyy) fila " + (i+1));
                return;
            }
        }

        filaEditando = -1;
        modelo.fireTableDataChanged();

        JOptionPane.showMessageDialog(this, "Datos guardados correctamente");
    }

    public static void main(String[] args) {
        new ClientesFrame().setVisible(true);
    }
}