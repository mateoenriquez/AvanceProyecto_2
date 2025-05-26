package ezservice;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EzServiceUI extends JFrame {
    private Hashtable<Integer, Usuario> usuarios = new Hashtable<>();
    private PriorityQueue<Ticket> tickets = new PriorityQueue<>();
    private int ticketIdCounter = 1;

    public EzServiceUI() {
        setTitle("EzService - Gestión de Usuarios y Tickets");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());
        tabbedPane.addTab("Gestión de Tickets", crearPanelTickets());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JLabel idLabel = new JLabel("ID:");
        JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        JLabel nombreLabel = new JLabel("Nombre:");
        JTextField nombreField = new JTextField();
        JLabel depLabel = new JLabel("Departamento:");
        JComboBox<String> departamentoBox = new JComboBox<>(new String[]{"Operativo", "Administrativo", "T.I"});
        JTextArea displayArea = new JTextArea(5, 30);
        displayArea.setEditable(false);

        JButton crearBtn = new JButton("Crear Usuario");
        crearBtn.addActionListener(e -> {
            int id = (int) idSpinner.getValue();
            String nombre = nombreField.getText().trim();
            String dep = (String) departamentoBox.getSelectedItem();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre requerido");
                return;
            }
            usuarios.put(id, new Usuario(id, nombre, dep));
            displayArea.setText("Usuario creado: " + usuarios.get(id));
        });

        JButton consultarBtn = new JButton("Consultar Usuario");
        consultarBtn.addActionListener(e -> {
            int id = (int) idSpinner.getValue();
            Usuario u = usuarios.get(id);
            if (u != null) {
                displayArea.setText(u.toString());
            } else {
                displayArea.setText("Usuario no encontrado.");
            }
        });

        JButton modificarBtn = new JButton("Modificar Usuario");
        modificarBtn.addActionListener(e -> {
            int id = (int) idSpinner.getValue();
            Usuario u = usuarios.get(id);
            if (u != null) {
                String nuevoNombre = nombreField.getText().trim();
                if (!nuevoNombre.isEmpty()) {
                    u.setNombre(nuevoNombre);
                }
                u.setDepartamento((String) departamentoBox.getSelectedItem());
                displayArea.setText("Usuario actualizado: " + u);
            } else {
                displayArea.setText("Usuario no encontrado.");
            }
        });

        JButton eliminarBtn = new JButton("Eliminar Usuario");
        eliminarBtn.addActionListener(e -> {
            int id = (int) idSpinner.getValue();
            String permiso = JOptionPane.showInputDialog(this, "Ingrese ID de un usuario administrativo:");
            try {
                int adminId = Integer.parseInt(permiso);
                Usuario admin = usuarios.get(adminId);
                if (admin != null && "Administrativo".equals(admin.getDepartamento())) {
                    usuarios.remove(id);
                    displayArea.setText("Usuario eliminado: ID " + id);
                } else {
                    JOptionPane.showMessageDialog(this, "Permiso denegado: no es administrador.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        panel.add(idLabel); panel.add(idSpinner);
        panel.add(nombreLabel); panel.add(nombreField);
        panel.add(depLabel); panel.add(departamentoBox);
        panel.add(crearBtn); panel.add(consultarBtn);
        panel.add(modificarBtn); panel.add(eliminarBtn);
        panel.add(new JScrollPane(displayArea));

        return panel;
    }

    private JPanel crearPanelTickets() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JLabel idUsuarioLabel = new JLabel("ID Usuario:");
        JSpinner userIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        JLabel tipoLabel = new JLabel("Tipo de Problema:");
        JComboBox<String> tipoBox = new JComboBox<>(new String[]{"Solicitud de equipo", "Equipo dañado", "Otro"});
        JTextArea displayArea = new JTextArea(5, 30);
        displayArea.setEditable(false);

        JButton crearTicketBtn = new JButton("Crear Ticket");
        crearTicketBtn.addActionListener(e -> {
            int userId = (int) userIdSpinner.getValue();
            Usuario u = usuarios.get(userId);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Usuario no existe. Ingrese uno válido.");
                return;
            }
            String tipo = (String) tipoBox.getSelectedItem();
            if ("Otro".equals(tipo)) {
                String input = JOptionPane.showInputDialog(this, "Describa el problema (máx 10 caracteres):");
                if (input == null || input.length() > 10) {
                    JOptionPane.showMessageDialog(this, "Descripción inválida.");
                    return;
                }
                tipo = "Otros";
            }
            Ticket t = new Ticket(ticketIdCounter++, userId, tipo, calcularPrioridad(u, tipo));
            tickets.add(t);
            displayArea.setText("Ticket creado: " + t);
        });

        JButton despacharBtn = new JButton("Despachar Ticket");
        despacharBtn.addActionListener(e -> {
            Ticket t = tickets.poll();
            if (t != null) {
                displayArea.setText("Ticket despachado: " + t);
            } else {
                displayArea.setText("No hay tickets pendientes.");
            }
        });

        JButton consultarBtn = new JButton("Consultar Ticket por ID");
        consultarBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese ID del ticket:");
            try {
                int id = Integer.parseInt(input);
                for (Ticket t : tickets) {
                    if (t.getId() == id) {
                        displayArea.setText("Ticket encontrado: " + t);
                        return;
                    }
                }
                displayArea.setText("Ticket no encontrado.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        JButton eliminarBtn = new JButton("Eliminar Ticket");
        eliminarBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese su ID de usuario:");
            try {
                int usuarioId = Integer.parseInt(input);
                Usuario solicitante = usuarios.get(usuarioId);
                if (solicitante == null) throw new Exception();

                String ticketInput = JOptionPane.showInputDialog(this, "Ingrese ID del ticket:");
                int ticketId = Integer.parseInt(ticketInput);
                Ticket encontrado = null;
                for (Ticket t : tickets) {
                    if (t.getId() == ticketId) {
                        encontrado = t;
                        break;
                    }
                }
                if (encontrado == null) {
                    displayArea.setText("Ticket no encontrado.");
                    return;
                }
                if (solicitante.getDepartamento().equals("Administrativo") || encontrado.getUserId() == usuarioId) {
                    tickets.remove(encontrado);
                    displayArea.setText("Ticket eliminado: " + encontrado);
                } else {
                    JOptionPane.showMessageDialog(this, "No tiene permisos para eliminar este ticket.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Entrada inválida.");
            }
        });

        panel.add(idUsuarioLabel); panel.add(userIdSpinner);
        panel.add(tipoLabel); panel.add(tipoBox);
        panel.add(crearTicketBtn); panel.add(despacharBtn);
        panel.add(consultarBtn); panel.add(eliminarBtn);
        panel.add(new JScrollPane(displayArea));

        return panel;
    }

    private int calcularPrioridad(Usuario u, String tipo) {
        int prioridadUsuario;
        switch (u.getDepartamento()) {
            case "Administrativo": prioridadUsuario = 1; break;
            case "Operativo": prioridadUsuario = 2; break;
            case "T.I": default: prioridadUsuario = 3; break;
        }

        int prioridadProblema;
        switch (tipo) {
            case "Otros": prioridadProblema = 1; break;
            case "Equipo dañado": prioridadProblema = 2; break;
            case "Solicitud de equipo": default: prioridadProblema = 3; break;
        }

        return prioridadUsuario + prioridadProblema;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EzServiceUI::new);
    }
}

