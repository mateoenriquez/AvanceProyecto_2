package ezservice;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private int edad;
    private String sede;
    private String area;
    private String cargo;

    public Usuario(int id, String nombre, String apellido, int edad, String sede, String area, String cargo) {
        this.id = id;
        this.nombre = formatearTexto(nombre);
        this.apellido = formatearTexto(apellido);
        this.edad = edad;
        this.sede = sede;
        this.area = area;
        this.cargo = cargo;
    }

    private String formatearTexto(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        texto = texto.toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public String getSede() {
        return sede;
    }

    public String getArea() {
        return area;
    }

    public String getCargo() {
        return cargo;
    }

    public void setNombre(String nombre) {
        this.nombre = formatearTexto(nombre);
    }

    public void setApellido(String apellido) {
        this.apellido = formatearTexto(apellido);
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getPrioridadCargo() {
        return switch (cargo) {
            case "Gerente regional" -> 1;
            case "Gerente de area" -> 2;
            case "Administrativo" -> 3;
            case "Operacional" -> 4;
            default -> 5;
        };
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + " " + apellido +
                ", Edad: " + edad + ", Sede: " + sede +
                ", Área: " + area + ", Cargo: " + cargo;
    }
}
