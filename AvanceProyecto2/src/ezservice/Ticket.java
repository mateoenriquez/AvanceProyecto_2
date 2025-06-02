package ezservice;

public class Ticket implements Comparable<Ticket> {
    private static int globalId = 1;

    private int id;
    private int usuarioId;
    private String categoria;
    private String subcategoria;
    private String descripcion;
    private String estado;
    private int prioridadTotal;
    private long timestamp;

    public Ticket(int usuarioId, String categoria, String subcategoria, String descripcion, int prioridadTotal) {
        this.id = globalId++;
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.estado = "No atendido";
        this.prioridadTotal = prioridadTotal;
        this.timestamp = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrioridadTotal() {
        return prioridadTotal;
    }

    @Override
    public int compareTo(Ticket other) {
        if (this.prioridadTotal != other.prioridadTotal) {
            return Integer.compare(this.prioridadTotal, other.prioridadTotal);
        }
        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return "Ticket ID: " + id +
                "\nUsuario ID: " + usuarioId +
                "\nCategoría: " + categoria +
                "\nSubcategoría: " + subcategoria +
                (descripcion != null && !descripcion.isEmpty() ? "\nDescripción: " + descripcion : "") +
                "\nEstado: " + estado;
    }
}
