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
        else if (entero > 999999) letras = "ERROR (Monto excede el límite) ";
        else letras = convertirEntero(entero);

        return "SON: " + letras + "CON " + String.format("%02d", centavos) + "/100 SOLES";
    }

    private static String convertirEntero(long n) {
        String s = "";
        int miles = (int) (n / 1000);
        int resto = (int) (n % 1000);

        if (miles > 0) {
            if (miles == 1) {
                s += "MIL ";
            } else {
                s += procesarTresDigitos(miles) + "MIL ";
            }
        }
        if (resto > 0) {
            s += procesarTresDigitos(resto);
        }
        return s;
    }

    private static String procesarTresDigitos(int n) {
        String s = "";
        int c = n / 100;
        int d = (n % 100) / 10;
        int u = n % 10;

        if (n == 100) return "CIEN ";

        s += CENTENAS[c];

        if (d == 1) {
            s += DECENAS[u];
        } else {
            if (d == 2) {
                if (u == 0) s += "VEINTE ";
                else s += "VEINTI" + UNIDADES[u].trim() + " ";
            } else if (d > 2) {
                s += DECENAS[d + 8]; // Aquí estaba la corrección clave del índice
                if (u > 0) s += "Y " + UNIDADES[u];
            } else {
                s += UNIDADES[u];
            }
        }
        return s;
    }
}