package proyecto.pos.controller;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Merma;
import proyecto.pos.service.MermaService;

public class MermaController {

    private MermaService mermaService;

    public MermaController(Connection conexion) {
        this.mermaService = new MermaService(conexion);
    }

    // ========================
    // REGISTRAR MERMA
    // ========================
    public void registrarMerma(Insumo insumo,
                               Empleado empleado,
                               double cantidad,
                               String motivo,
                               Date fechaRegistro) {

        Merma merma = new Merma();

        merma.setInsumo(insumo);
        merma.setEmpleado(empleado);
        merma.setCantidad(cantidad);
        merma.setMotivo(motivo);
        merma.setFecha_registro(fechaRegistro);

        mermaService.registrarMerma(merma);
    }

    // ========================
    // OBTENER MERMA
    // ========================
    public Merma obtenerPorId(int id) {
        return mermaService.obtenerMermaPorId(id);
    }

    // ========================
    // LISTAR MERMAS
    // ========================
    public List<Merma> listarMermas() {
        return mermaService.listarMermas();
    }

    // ========================
    // ACTUALIZAR MERMA
    // ========================
    public void actualizarMerma(int mermaId,
                                Insumo insumo,
                                Empleado empleado,
                                double cantidad,
                                String motivo,
                                Date fechaRegistro) {

        Merma merma = new Merma();

        merma.setMermaId(mermaId);
        merma.setInsumo(insumo);
        merma.setEmpleado(empleado);
        merma.setCantidad(cantidad);
        merma.setMotivo(motivo);
        merma.setFecha_registro(fechaRegistro);

        mermaService.actualizarMerma(merma);
    }

    // ========================
    // ELIMINAR MERMA
    // ========================
    public void eliminarMerma(int mermaId) {
        mermaService.eliminarMermaPorId(mermaId);
    }

    // ========================
    // REGLAS DE NEGOCIO
    // ========================
    public boolean esMermaAlta(Merma merma) {
        return mermaService.esMermaAlta(merma);
    }
    
    
    public boolean tieneMotivo(Merma merma, String texto) {
        return mermaService.tieneMotivo(merma, texto);
    }
}