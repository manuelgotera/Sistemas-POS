package proyecto.pos.model;

/**
 * Modelo de dominio: Proveedor Regional
 * HU01 - Registro de proveedores regionales
 */
public class Proveedor {

    private int     id;
    private String  codigo;
    private String  nombre;
    private String  rucDni;
    private String  telefono;
    private String  direccion;
    private String  tipoInsumo;
    private String  region;
    private String  contacto;
    private String  email;
    private int     cumplimiento; // porcentaje 0-100
    private boolean activo;

    // ── Constructor vacío ──────────────────────────────────────────────────────
    public Proveedor() {}

    // ── Constructor completo ───────────────────────────────────────────────────
    public Proveedor(int id, String codigo, String nombre, String rucDni,
                     String telefono, String direccion, String tipoInsumo,
                     String region, String contacto, String email, int cumplimiento, boolean activo) {
        this.id           = id;
        this.codigo       = codigo;
        this.nombre       = nombre;
        this.rucDni       = rucDni;
        this.telefono     = telefono;
        this.direccion    = direccion;
        this.tipoInsumo   = tipoInsumo;
        this.region       = region;
        this.contacto     = contacto;
        this.email        = email;
        this.cumplimiento = cumplimiento;
        this.activo       = activo;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public int     getId()           { return id; }
    public String  getCodigo()       { return codigo; }
    public String  getNombre()       { return nombre; }
    public String  getRucDni()       { return rucDni; }
    public String  getTelefono()     { return telefono; }
    public String  getDireccion()    { return direccion; }
    public String  getTipoInsumo()   { return tipoInsumo; }
    public String  getRegion()       { return region; }
    public String  getContacto()     { return contacto;}
    public String  getEmail()        { return email;}   
    public int     getCumplimiento() { return cumplimiento; }
    public boolean isActivo()        { return activo; }
    

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setId(int id)                    { this.id           = id; }
    public void setCodigo(String codigo)          { this.codigo       = codigo; }
    public void setNombre(String nombre)          { this.nombre       = nombre; }
    public void setRucDni(String rucDni)          { this.rucDni       = rucDni; }
    public void setTelefono(String telefono)      { this.telefono     = telefono; }
    public void setDireccion(String direccion)    { this.direccion    = direccion; }
    public void setTipoInsumo(String tipoInsumo)  { this.tipoInsumo   = tipoInsumo; }
    public void setRegion(String region)          { this.region       = region; }
    public void setCumplimiento(int cumplimiento) { this.cumplimiento = cumplimiento; }
    public void setEmail(String email)            { this.email = email; }
    public void setActivo(boolean activo)         { this.activo       = activo; }
    public void setContacto(String contacto)    {this.contacto      = contacto;}

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return nombre + " (" + rucDni + ")";
    }
}