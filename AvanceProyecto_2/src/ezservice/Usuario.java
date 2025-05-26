package ezservice;

class Usuario  {
    private int id;
    private String nombre;
    private String departamento;

    public Usuario(int id, String nombre, String departamento) {
        this.id = id;
        this.nombre = nombre;
        this.departamento = departamento;
    }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getDepartamento() { return departamento; }
    public int getId() { return id; }
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Departamento: " + departamento;
    }
}