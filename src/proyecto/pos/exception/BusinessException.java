package proyecto.pos.exception;

/**
 * Excepción personalizada para errores de lógica de negocio
 */
public class BusinessException extends Exception {

    public BusinessException(String mensaje) {
        super(mensaje);
    }

    public BusinessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
