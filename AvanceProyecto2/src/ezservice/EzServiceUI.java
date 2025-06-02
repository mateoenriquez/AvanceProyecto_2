package ezservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class EzServiceUI extends JFrame {
    private UsuarioManager usuarioManager = new UsuarioManager();
    private TicketManager ticketManager = new TicketManager();

    private JTabbedPane tabbedPane;

    public EzServiceUI() {
        setTitle("EzService - Sistema de Gestión");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());
        tabbedPane.addTab("Crear Tickets", crearPanelCrearTickets());

        // Placeholder temporal para acceso restringido
        tabbedPane.addTab("Gestión de Tickets", new JPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) {
                String input = JOptionPane.showInputDialog(this, "Ingrese su ID para acceder:");
                try {
                    int id = Integer.parseInt(input);
                    Usuario user = usuarioManager.getUsuario(id);
                    if (user != null && user.getArea().equals("I.T") && user.getPrioridadCargo() <= 3) {
                        tabbedPane.setComponentAt(2, crearPanelGestionTickets());
                    } else {
                        JOptionPane.showMessageDialog(this, "Acceso denegado. Solo personal de I.T con prioridad alta.");
                        tabbedPane.setSelectedIndex(0);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ID inválido.");
                    tabbedPane.setSelectedIndex(0);
                }
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        JTextField nombreField = new JTextField();
        JTextField apellidoField = new JTextField();
        JSpinner edadSpinner = new JSpinner(new SpinnerNumberModel(18, 18, 65, 1));

        JComboBox<String> sedeBox = new JComboBox<>(new String[]{"Seleccionar Sede", "Quito", "Coca"});
        JComboBox<String> areaBox = new JComboBox<>(new String[]{"Seleccionar Área", "Finanzas", "I.T", "Sperry", "IGAPO", "RRHH"});
        JComboBox<String> cargoBox = new JComboBox<>(new String[]{"Seleccionar Cargo", "Gerente regional", "Gerente de area", "Administrativo", "Operacional"});

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idSpinner);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Apellido:"));
        formPanel.add(apellidoField);
        formPanel.add(new JLabel("Edad:"));
        formPanel.add(edadSpinner);
        formPanel.add(new JLabel("Sede:"));
        formPanel.add(sedeBox);
        formPanel.add(new JLabel("Área:"));
        formPanel.add(areaBox);
        formPanel.add(new JLabel("Cargo:"));
        formPanel.add(cargoBox);

        JTextArea resultadoArea = new JTextArea(5, 40);
        resultadoArea.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(resultadoArea);

        JPanel botonesPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        JButton crearBtn = new JButton("Crear Usuario");
        crearBtn.addActionListener(e -> {
            int id = (int) idSpinner.getValue();
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            int edad = (int) edadSpinner.getValue();
            String sede = (String) sedeBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();
            String cargo = (String) cargoBox.getSelectedItem();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre no ingresado.");
                return;
            }
            if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
                JOptionPane.showMessageDialog(this, "Ingresar nombre válido.");
                return;
            }
            if (apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Apellido no ingresado.");
                return;
            }
            if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ]+")) {
                JOptionPane.showMessageDialog(this, "Ingresar apellido válido.");
                return;
            }
            if (edad < 18 || edad > 65) {
                JOptionPane.showMessageDialog(this, "Ingresar una edad entre 18 y 65 años.");
                return;
            }
            if (sede.equals("Seleccionar Sede")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción en el listado de sede.");
                return;
            }
            if (area.equals("Seleccionar Área")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción en el listado de área.");
                return;
            }
            if (cargo.equals("Seleccionar Cargo")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción en el listado de cargo.");
                return;
            }

            boolean yaExiste = usuarioManager.existeUsuario(id);
            boolean continuar = true;

            if (yaExiste) {
                String input = JOptionPane.showInputDialog(this, "Ya existe un usuario con este ID. Ingrese su ID para verificar si tiene permisos:");
                try {
                    int idSolicitante = Integer.parseInt(input);
                    if (usuarioManager.puedeModificar(idSolicitante)) {
                        int r = JOptionPane.showConfirmDialog(this, "¿Desea reemplazar los datos del usuario con ID " + id + "?", "Confirmación", JOptionPane.YES_NO_OPTION);
                        continuar = r == JOptionPane.YES_OPTION;
                    } else {
                        JOptionPane.showMessageDialog(this, "No tiene permisos para reemplazar el usuario.");
                        continuar = false;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "ID inválido.");
                    continuar = false;
                }
            }

            if (continuar) {
                Usuario nuevo = new Usuario(id, nombre, apellido, edad, sede, area, cargo);
                usuarioManager.agregarOActualizarUsuario(nuevo, true);
                resultadoArea.setText("Usuario creado/modificado:\n" + nuevo.toString());

                nombreField.setText("");
                apellidoField.setText("");
                edadSpinner.setValue(18);
                sedeBox.setSelectedIndex(0);
                areaBox.setSelectedIndex(0);
                cargoBox.setSelectedIndex(0);
            }
        });

        JButton consultarIDBtn = new JButton("Consultar por ID");
        consultarIDBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese ID del usuario a consultar:");
            try {
                int id = Integer.parseInt(input);
                Usuario u = usuarioManager.getUsuario(id);
                if (u != null) {
                    resultadoArea.setText("Usuario encontrado:\n" + u.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        JComboBox<String> areaConsultaBox = new JComboBox<>(new String[]{"Seleccionar Área", "Finanzas", "I.T", "Sperry", "IGAPO", "RRHH"});
        JButton consultarAreaBtn = new JButton("Consultar por Área");
        consultarAreaBtn.addActionListener(e -> {
            String areaSel = (String) areaConsultaBox.getSelectedItem();
            if (areaSel.equals("Seleccionar Área")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción en el listado.");
                return;
            }
            List<Usuario> lista = usuarioManager.getUsuariosPorArea(areaSel);
            if (lista.isEmpty()) {
                resultadoArea.setText("No hay personal registrado en el área seleccionada.");
            } else {
                StringBuilder sb = new StringBuilder("Personal del área " + areaSel + ":\n");
                for (Usuario u : lista) sb.append(u.toString()).append("\\n");
                resultadoArea.setText(sb.toString());
            }
        });

        JButton modificarBtn = new JButton("Modificar Usuario");
        modificarBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese su ID para verificar permisos:");
            try {
                int id = Integer.parseInt(input);
                if (usuarioManager.puedeModificar(id)) {
                    JOptionPane.showMessageDialog(this, "Tiene acceso. Puede modificar usando el botón CREAR (sobrescribirá).");
                } else {
                    JOptionPane.showMessageDialog(this, "No tiene permisos para modificar usuarios.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        JButton eliminarBtn = new JButton("Eliminar Usuario");
        eliminarBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese su ID para verificar permisos:");
            try {
                int idSolicitante = Integer.parseInt(input);
                if (!usuarioManager.puedeModificar(idSolicitante)) {
                    JOptionPane.showMessageDialog(this, "No tiene permisos para eliminar usuarios.");
                    return;
                }
                String inputEliminar = JOptionPane.showInputDialog(this, "Ingrese el ID del usuario que desea eliminar:");
                int idEliminar = Integer.parseInt(inputEliminar);
                boolean eliminado = usuarioManager.eliminarUsuario(idEliminar, idSolicitante);
                if (eliminado) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario no encontrado o no se pudo eliminar.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        botonesPanel.add(crearBtn);
        botonesPanel.add(consultarIDBtn);
        botonesPanel.add(modificarBtn);
        botonesPanel.add(eliminarBtn);

        JPanel consultaAreaPanel = new JPanel();
        consultaAreaPanel.add(areaConsultaBox);
        consultaAreaPanel.add(consultarAreaBtn);

        panel.add(formPanel);
        panel.add(botonesPanel);
        panel.add(consultaAreaPanel);
        panel.add(scrollResultado);

        return panel;
    }

    private JPanel crearPanelCrearTickets() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Formulario de Ticket"));

        JSpinner idUserSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        JComboBox<String> categoriaBox = new JComboBox<>(new String[]{
                "Seleccionar categoría", "Inconveniente con software", "Inconveniente con hardware",
                "Soporte técnico", "Solicitar ítem"
        });
        JComboBox<String> subcategoriaBox = new JComboBox<>(new String[]{"Seleccionar subcategoría"});
        JTextField descripcionField = new JTextField();

        formPanel.add(new JLabel("ID Usuario:"));
        formPanel.add(idUserSpinner);
        formPanel.add(new JLabel("Categoría:"));
        formPanel.add(categoriaBox);
        formPanel.add(new JLabel("Subcategoría:"));
        formPanel.add(subcategoriaBox);
        formPanel.add(new JLabel("Descripción (Opcional):"));
        formPanel.add(descripcionField);

        categoriaBox.addActionListener(e -> {
            String categoria = (String) categoriaBox.getSelectedItem();
            subcategoriaBox.removeAllItems();
            subcategoriaBox.addItem("Seleccionar subcategoría");

            if (categoria.equals("Inconveniente con software")) {
                subcategoriaBox.addItem("Software de seguridad");
                subcategoriaBox.addItem("Conectividad de red");
                subcategoriaBox.addItem("Aplicacion especifica");
                subcategoriaBox.addItem("Sistema operativo");
                subcategoriaBox.addItem("Actualizacion de software");
            } else if (categoria.equals("Inconveniente con hardware")) {
                subcategoriaBox.addItem("Equipo de computo");
                subcategoriaBox.addItem("Periferico");
                subcategoriaBox.addItem("Dispositivos moviles");
                subcategoriaBox.addItem("Redes (Router, switches, puntos de acceso y cables de red)");
                subcategoriaBox.addItem("Servidores");
                subcategoriaBox.addItem("Almacemamiento externo");
            } else if (categoria.equals("Soporte técnico")) {
                subcategoriaBox.addItem("Configuracion/instalacion");
                subcategoriaBox.addItem("Acceso permisos");
                subcategoriaBox.addItem("Asesoria/Consultoria");
                subcategoriaBox.addItem("Limpieza/Mantenimiento");
                subcategoriaBox.addItem("Formateo/Reinstalacion");
            } else if (categoria.equals("Solicitar ítem")) {
                subcategoriaBox.addItem("Hardware");
                subcategoriaBox.addItem("Software");
                subcategoriaBox.addItem("Acceso a recursos");
                subcategoriaBox.addItem("Cuentas de usuarios");
            }
        });

        JButton ingresarBtn = new JButton("Ingresar Ticket");
        ingresarBtn.addActionListener(e -> {
            int idUser = (int) idUserSpinner.getValue();
            Usuario usuario = usuarioManager.getUsuario(idUser);

            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "El usuario con ID ingresado no existe.");
                return;
            }

            String categoria = (String) categoriaBox.getSelectedItem();
            if (categoria.equals("Seleccionar categoría")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción válida de categoría.");
                return;
            }

            String subcategoria = (String) subcategoriaBox.getSelectedItem();
            if (subcategoria == null || subcategoria.equals("Seleccionar subcategoría")) {
                JOptionPane.showMessageDialog(this, "Seleccionar una opción válida de subcategoría.");
                return;
            }

            String descripcion = descripcionField.getText().trim();
            if (!descripcion.isEmpty() && descripcion.length() > 50) {
                JOptionPane.showMessageDialog(this, "La descripción no puede tener más de 50 caracteres.");
                return;
            }

            int prioridad = calcularPrioridadTicket(usuario, categoria, subcategoria);

            Ticket nuevo = new Ticket(idUser, categoria, subcategoria, descripcion, prioridad);
            ticketManager.agregarTicket(nuevo);

            JOptionPane.showMessageDialog(this,
                    "Ticket generado exitosamente.\\n\\n" + nuevo.toString(),
                    "Ticket Creado", JOptionPane.INFORMATION_MESSAGE
            );

            categoriaBox.setSelectedIndex(0);
            subcategoriaBox.removeAllItems();
            subcategoriaBox.addItem("Seleccionar subcategoría");
            descripcionField.setText("");
        });

        JPanel botonesPanel = new JPanel();
        botonesPanel.add(ingresarBtn);

        panel.add(formPanel);
        panel.add(botonesPanel);

        return panel;
    }

    private int calcularPrioridadTicket(Usuario user, String categoria, String subcategoria) {
        int prioridadCargo = user.getPrioridadCargo();
        int prioridadCategoria = switch (categoria) {
            case "Inconveniente con software", "Inconveniente con hardware" -> 1;
            case "Soporte técnico" -> 2;
            case "Solicitar ítem" -> 3;
            default -> 4;
        };

        int prioridadSub = 4;

        switch (categoria) {
            case "Inconveniente con software" -> {
                if (subcategoria.equals("Software de seguridad")) prioridadSub = 1;
                else if (subcategoria.equals("Conectividad de red")) prioridadSub = 2;
                else if (subcategoria.equals("Aplicacion especifica") || subcategoria.equals("Sistema operativo")) prioridadSub = 3;
                else if (subcategoria.equals("Actualizacion de software")) prioridadSub = 4;
            }
            case "Inconveniente con hardware" -> {
                if (subcategoria.contains("Redes") || subcategoria.equals("Servidores")) prioridadSub = 1;
                else if (subcategoria.equals("Equipo de computo") || subcategoria.equals("Dispositivos moviles")) prioridadSub = 2;
                else prioridadSub = 3;
            }
            case "Soporte técnico" -> {
                if (subcategoria.equals("Configuracion/instalacion") || subcategoria.equals("Formateo/Reinstalacion")) prioridadSub = 1;
                else if (subcategoria.equals("Acceso permisos") || subcategoria.equals("Limpieza/Mantenimiento")) prioridadSub = 2;
                else prioridadSub = 3;
            }
            case "Solicitar ítem" -> {
                if (subcategoria.equals("Hardware")) prioridadSub = 1;
                else if (subcategoria.equals("Software") || subcategoria.equals("Acceso a recursos")) prioridadSub = 2;
                else prioridadSub = 3;
            }
        }

        return prioridadCargo * 100 + prioridadCategoria * 10 + prioridadSub;
    }

    private JPanel crearPanelGestionTickets() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea areaTickets = new JTextArea(20, 50);
        areaTickets.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTickets);

        JPanel accionesPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton mostrarBtn = new JButton("Mostrar tickets no atendidos");
        mostrarBtn.addActionListener(e -> {
            List<Ticket> lista = ticketManager.getTicketsNoAtendidosOrdenados();
            if (lista.isEmpty()) {
                areaTickets.setText("No hay tickets pendientes.");
                return;
            }
            StringBuilder sb = new StringBuilder("Tickets no atendidos:\n\n");
            for (Ticket t : lista) sb.append(t).append("\n-------------------\n");
            areaTickets.setText(sb.toString());
        });

        JTextField ticketIdField = new JTextField();
        JButton buscarIdBtn = new JButton("Mostrar ticket por ID");
        buscarIdBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(ticketIdField.getText().trim());
                Ticket t = ticketManager.buscarTicketPorId(id);
                if (t != null) {
                    areaTickets.setText("Ticket encontrado:\n\n" + t.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "Ticket no encontrado.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un ID válido.");
            }
        });

        JComboBox<String> estadoBox = new JComboBox<>(new String[]{"En proceso", "En Espera", "Resuelto", "Eliminado"});
        JTextField descripcionDespacho = new JTextField();

        JButton despacharBtn = new JButton("Despachar ticket");
        despacharBtn.addActionListener(e -> {
            String estado = (String) estadoBox.getSelectedItem();
            String desc = descripcionDespacho.getText().trim();
            if (desc.isEmpty() || desc.length() > 50) {
                JOptionPane.showMessageDialog(this, "Descripción obligatoria (máx 50 caracteres).");
                return;
            }
            Ticket despachado = ticketManager.despacharTicket(estado, desc);
            if (despachado != null) {
                JOptionPane.showMessageDialog(this, "Ticket despachado:\n\n" + despachado.toString());
            } else {
                JOptionPane.showMessageDialog(this, "No hay tickets para despachar.");
            }
        });

        JComboBox<String> estadoFiltro = new JComboBox<>(new String[]{"En proceso", "En Espera", "Resuelto", "Eliminado"});
        JButton mostrarPorEstadoBtn = new JButton("Mostrar por estado");
        mostrarPorEstadoBtn.addActionListener(e -> {
            String estado = (String) estadoFiltro.getSelectedItem();
            List<Ticket> lista = ticketManager.getTicketsPorEstado(estado);
            if (lista.isEmpty()) {
                areaTickets.setText("No hay tickets en estado: " + estado);
                return;
            }
            StringBuilder sb = new StringBuilder("Tickets en estado '" + estado + "':\n\n");
            for (Ticket t : lista) sb.append(t).append("\n-------------------\n");
            areaTickets.setText(sb.toString());
        });

        JTextField descripcionActualizacion = new JTextField();
        JButton actualizarEstadoBtn = new JButton("Actualizar estado");
        actualizarEstadoBtn.addActionListener(e -> {
            String estado = (String) estadoBox.getSelectedItem();
            String desc = descripcionActualizacion.getText().trim();
            if (desc.isEmpty() || desc.length() > 50) {
                JOptionPane.showMessageDialog(this, "Descripción obligatoria (máx 50 caracteres).");
                return;
            }
            Ticket actualizado = ticketManager.actualizarTicketEstado(estado, desc);
            if (actualizado != null) {
                JOptionPane.showMessageDialog(this, "Ticket actualizado:\n\n" + actualizado.toString());
            } else {
                JOptionPane.showMessageDialog(this, "No hay tickets en estado En Proceso o En Espera.");
            }
        });

        JTextField idEliminarField = new JTextField();
        JTextField descEliminarField = new JTextField();
        JButton eliminarBtn = new JButton("Eliminar ticket");
        eliminarBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idEliminarField.getText().trim());
                String desc = descEliminarField.getText().trim();
                if (desc.isEmpty() || desc.length() > 50) {
                    JOptionPane.showMessageDialog(this, "Ingrese una descripción válida para la eliminación.");
                    return;
                }
                boolean exito = ticketManager.eliminarPorIdAdministrador(id, desc);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Ticket eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el ticket con ese ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        // Agregar todos los botones y campos
        accionesPanel.add(new JLabel("Buscar ID Ticket:"));
        accionesPanel.add(ticketIdField);
        accionesPanel.add(buscarIdBtn);
        accionesPanel.add(mostrarBtn);
        accionesPanel.add(new JLabel("Filtrar por estado:"));
        accionesPanel.add(estadoFiltro);
        accionesPanel.add(mostrarPorEstadoBtn);

        JPanel despachoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        despachoPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Ticket"));
        despachoPanel.add(new JLabel("Estado:"));
        despachoPanel.add(estadoBox);
        despachoPanel.add(new JLabel("Descripción (máx 50):"));
        despachoPanel.add(descripcionDespacho);
        despachoPanel.add(despacharBtn);
        despachoPanel.add(actualizarEstadoBtn);

        JPanel eliminarPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        eliminarPanel.setBorder(BorderFactory.createTitledBorder("Eliminar ticket"));
        eliminarPanel.add(new JLabel("ID Ticket a eliminar:"));
        eliminarPanel.add(idEliminarField);
        eliminarPanel.add(new JLabel("Motivo (máx 50):"));
        eliminarPanel.add(descEliminarField);
        eliminarPanel.add(eliminarBtn);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(accionesPanel, BorderLayout.NORTH);
        contenedor.add(despachoPanel, BorderLayout.CENTER);
        contenedor.add(eliminarPanel, BorderLayout.SOUTH);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(contenedor, BorderLayout.EAST);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EzServiceUI::new);
    }
}

