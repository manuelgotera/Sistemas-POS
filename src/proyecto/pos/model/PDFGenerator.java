package proyecto.pos.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;

public class PDFGenerator {

    public static void crearBoleta(Venta v) {
        Document doc = new Document(PageSize.A6, 15, 15, 20, 20);
        try {
            String nombreArchivo = "Boleta_TRX_" + v.getVentaId() + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();

            Font fBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font fNormal = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            Font fSmall = new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC);

            Paragraph p = new Paragraph("LA BUENA VIDA RESTOBAR\n", fBold);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            
            p = new Paragraph("RUC: 20601234567\nAv. Larco 543 - Trujillo\n--------------------------------------\n", fNormal);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);

            doc.add(new Paragraph("BOLETA ELECTRÓNICA: B001-" + String.format("%05d", v.getVentaId()), fBold));
            doc.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getFecha()), fNormal));
            doc.add(new Paragraph("Cajero: " + (v.getEmpleado() != null ? v.getEmpleado().getNombre() : "Cajero"), fNormal));
            doc.add(new Paragraph("Mesa: " + (v.getMesa() != null && v.getMesa().getMesaId() > 0 ? v.getMesa().getMesaId() : "Llevar"), fNormal));
            doc.add(new Paragraph("--------------------------------------\n", fNormal));

            for (VentaDetalle det : v.getDetalles()) {
                doc.add(new Paragraph(String.format("%-2d x %-15s S/ %6.2f", 
                        det.getCantidad(), det.getPlato().getNombre(), det.getSubtotal()), fNormal));
            }

            doc.add(new Paragraph("\n--------------------------------------", fNormal));
            doc.add(new Paragraph("SUBTOTAL: S/ " + String.format("%.2f", v.getSubtotal()), fNormal));
            doc.add(new Paragraph("IGV (18%): S/ " + String.format("%.2f", v.getIgv()), fNormal));
            doc.add(new Paragraph("TOTAL:    S/ " + String.format("%.2f", v.getTotal()), fBold));
            doc.add(new Paragraph("\n" + NumeroALetras.convertir(v.getTotal()), fSmall));
            
            doc.add(new Paragraph("\nRepresentación impresa de la Boleta de Venta Electrónica.", fSmall));

            doc.close();
            java.awt.Desktop.getDesktop().open(new java.io.File(nombreArchivo)); 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}