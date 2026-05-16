package proyecto.pos.model;

public class NumeroALetras {
    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private static final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private static final String[] CENTENAS = {"", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    public static String convertir(double cantidad) {
        long entero = (long) cantidad;
        int centavos = (int) (Math.round((cantidad - entero) * 100));
        String letras = "";

        if (entero == 0) letras = "CERO ";
        else if (entero == 100) letras = "CIEN ";
        else letras = convertirEntero(entero);

        return "SON: " + letras + "CON " + String.format("%02d", centavos) + "/100 SOLES";
    }

    private static String convertirEntero(long n) {
        if (n >= 1000) return "ERROR (Solo montos menores a 1000 para este ejemplo)";
        String s = "";
        int c = (int) (n / 100);
        int d = (int) ((n % 100) / 10);
        int u = (int) (n % 10);

        s += CENTENAS[c];
        if (d == 1) s += DECENAS[u];
        else {
            s += (d == 2) ? "VEINTI" : (d > 2) ? DECENAS[d - 1] + (u > 0 ? "Y " : "") : "";
            if (d != 2) s += UNIDADES[u];
            else s += UNIDADES[u].trim();
        }
        return s;
    }
}
