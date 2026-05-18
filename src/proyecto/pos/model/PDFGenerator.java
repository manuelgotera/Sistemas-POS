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
    
    // Añade esto a tu clase PDFGenerator
    public static void generarReporteStock(javax.swing.JTable tabla) {
        // Usamos A4 rotado (horizontal) para que entren las 14 columnas
        Document doc = new Document(PageSize.A4.rotate(), 15, 15, 20, 20);
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String nombreArchivo = "Reporte_Stock_" + timestamp + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();

            Font fTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fHeader = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
            Font fCelda = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

            Paragraph titulo = new Paragraph("REPORTE DE INVENTARIO Y STOCK", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            doc.add(titulo);

            // Crear tabla PDF con el mismo número de columnas que tu JTable
            int numColumnas = tabla.getColumnCount();
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(numColumnas);
            pdfTable.setWidthPercentage(100);

            // Agregar los encabezados de las columnas
            for (int i = 0; i < numColumnas; i++) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Phrase(tabla.getColumnName(i), fHeader));
                cell.setBackgroundColor(new BaseColor(26, 83, 160)); // Tu color AZUL
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            // Agregar los datos de las filas
            for (int i = 0; i < tabla.getRowCount(); i++) {
                for (int j = 0; j < numColumnas; j++) {
                    Object valorCelda = tabla.getValueAt(i, j);
                    String texto = (valorCelda != null) ? valorCelda.toString() : "";
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Phrase(texto, fCelda));
                    cell.setPadding(4);
                    pdfTable.addCell(cell);
                }
            }

            doc.add(pdfTable);
            doc.close();
            
            // Abrir el archivo automáticamente
            java.awt.Desktop.getDesktop().open(new java.io.File(nombreArchivo));

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
    }
    
}