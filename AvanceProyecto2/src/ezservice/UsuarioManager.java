package ezservice;

import javax.swing.*;
import java.util.*;

public class UsuarioManager {
    private Map<Integer, Usuario> usuarios = new HashMap<>();

    public boolean existeUsuario(int id) {
        return usuarios.containsKey(id);
    }

    public Usuario getUsuario(int id) {
        return usuarios.get(id);
    }

    public Collection<Usuario> getTodosUsuarios() {
        return usuarios.values();
    }

    public List<Usuario> getUsuariosPorArea(String area) {
        List<Usuario> lista = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (u.getArea().equals(area)) {
                lista.add(u);
            }
        }
        return lista;
    }

    public boolean puedeModificar(int idSolicitante) {
        Usuario u = usuarios.get(idSolicitante);
        if (u == null) return false;
        int prioridad = u.getPrioridadCargo();
        return prioridad >= 1 && prioridad <= 3;
    }

    public void agregarOActualizarUsuario(Usuario nuevo, boolean forzarRemplazo) {
        if (forzarRemplazo || !usuarios.containsKey(nuevo.getId())) {
            usuarios.put(nuevo.getId(), nuevo);
        }
    }

    public boolean eliminarUsuario(int idEliminar, int idSolicitante) {
        Usuario solicitante = usuarios.get(idSolicitante);
        if (solicitante == null) return false;
        int prioridad = solicitante.getPrioridadCargo();
        if (prioridad > 3) return false;
        return usuarios.remove(idEliminar) != null;
    }
}
