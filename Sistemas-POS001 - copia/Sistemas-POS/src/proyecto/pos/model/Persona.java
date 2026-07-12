/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
    public abstract class Persona {

        protected int id;
        protected String nombre;
        protected String apellidos;
        protected String dni;
        protected String telefono;
        protected String email;

        public Persona(){

        }

        public Persona(String nombre, String apellido, String dni, String telefono, String email) {
            this.nombre = nombre;
            this.apellidos = apellido;
            this.dni = dni;
            this.telefono = telefono;
            this.email = email;
        }

        public Persona(int id, String nombre, String apellido, String dni, String telefono, String email) {
            this.id = id;
            this.nombre = nombre;
            this.apellidos = apellido;
            this.dni = dni;
            this.telefono = telefono;
            this.email = email;
        }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
    
    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", dni='" + dni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
