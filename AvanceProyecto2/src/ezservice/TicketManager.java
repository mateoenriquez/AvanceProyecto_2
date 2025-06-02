package ezservice;

import java.util.*;

public class TicketManager {
    private List<Ticket> tickets = new ArrayList<>();
    private Deque<Ticket> pilaTicketsEliminados = new ArrayDeque<>();

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public List<Ticket> getTicketsPorUsuario(int usuarioId) {
        List<Ticket> resultado = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getUsuarioId() == usuarioId) {
                resultado.add(t);
            }
        }
        resultado.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp())); // Último primero
        return resultado;
    }

    public Ticket buscarTicketPorId(int id) {
        for (Ticket t : tickets) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public boolean eliminarTicket(int idTicket, int idUsuario) {
        Iterator<Ticket> it = tickets.iterator();
        while (it.hasNext()) {
            Ticket t = it.next();
            if (t.getId() == idTicket && t.getUsuarioId() == idUsuario) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public List<Ticket> getTicketsNoAtendidosOrdenados() {
        List<Ticket> pendientes = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getEstado().equals("No atendido")) {
                pendientes.add(t);
            }
        }
        Collections.sort(pendientes); // Usa compareTo para orden por prioridad
        return pendientes;
    }

    public List<Ticket> getTicketsPorEstado(String estado) {
        List<Ticket> resultado = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getEstado().equals(estado)) {
                resultado.add(t);
            }
        }

        if (estado.equals("Eliminado")) {
            resultado.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp())); // Pila: último primero
        } else {
            resultado.sort(Comparator.naturalOrder()); // Orden por prioridad
        }
        return resultado;
    }

    public Ticket despacharTicket(String nuevoEstado, String descripcion) {
        List<Ticket> cola = getTicketsNoAtendidosOrdenados();
        if (cola.isEmpty()) return null;
        Ticket siguiente = cola.get(0);
        siguiente.setEstado(nuevoEstado);
        siguiente.setDescripcion(descripcion);
        return siguiente;
    }

    public Ticket actualizarTicketEstado(String nuevoEstado, String descripcion) {
        for (Ticket t : tickets) {
            if ("En proceso".equals(t.getEstado()) || "En Espera".equals(t.getEstado())) {
                t.setEstado(nuevoEstado);
                t.setDescripcion(descripcion);
                return t;
            }
        }
        return null;
    }

    public boolean eliminarPorIdAdministrador(int idTicket, String descripcion) {
        Iterator<Ticket> it = tickets.iterator();
        while (it.hasNext()) {
            Ticket t = it.next();
            if (t.getId() == idTicket) {
                t.setEstado("Eliminado");
                t.setDescripcion(descripcion);
                pilaTicketsEliminados.push(t);
                it.remove();
                return true;
            }
        }
        return false;
    }

    public List<Ticket> getTicketsEliminados() {
        return new ArrayList<>(pilaTicketsEliminados);
    }
}
