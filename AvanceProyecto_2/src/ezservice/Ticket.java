package ezservice;

class Ticket  implements Comparable<Ticket> {
    private int id;
    private int userId;
    private String tipo;
    private int prioridad;

    public Ticket(int id, int userId, String tipo, int prioridad) {
        this.id = id;
        this.userId = userId;
        this.tipo = tipo;
        this.prioridad = prioridad;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }

    public int compareTo(Ticket o) {
        return Integer.compare(this.prioridad, o.prioridad);
    }

    public String toString() {
        return "Ticket ID: " + id + ", Usuario ID: " + userId + ", Tipo: " + tipo + ", Prioridad: " + prioridad;
    }
}