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
    private int     cumplimiento; // porcentaje 0-100
    private boolean activo;

    // ── Constructor vacío ──────────────────────────────────────────────────────
    public Proveedor() {}

    // ── Constructor completo ───────────────────────────────────────────────────
    public Proveedor(int id, String codigo, String nombre, String rucDni,
                     String telefono, String direccion, String tipoInsumo,
                     String region, int cumplimiento, boolean activo) {
        this.id           = id;
        this.codigo       = codigo;
        this.nombre       = nombre;
        this.rucDni       = rucDni;
        this.telefono     = telefono;
        this.direccion    = direccion;
        this.tipoInsumo   = tipoInsumo;
        this.region       = region;
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
    public void setActivo(boolean activo)         { this.activo       = activo; }

    // ── toString ───────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return nombre + " (" + rucDni + ")";
    }

    public int getProveedorId() {
        return getId();
    }

    public void setProveedorId(int proveedorId) {
        setId(proveedorId);
    }

    public String getNombre_empresa() {
        return getNombre();
    }

    public void setNombre_empresa(String nombreEmpresa) {
        setNombre(nombreEmpresa);
    }
}