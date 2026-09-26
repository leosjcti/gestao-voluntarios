package br.com.ibaji.voluntarios.service;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DocumentoExtractorService {

    public String extrairTexto(File arquivoPdf) {
        try (PDDocument document = PDDocument.load(arquivoPdf, MemoryUsageSetting.setupTempFileOnly())) {
            if (document.isEncrypted()) {
                throw new IllegalStateException("O PDF está protegido por senha. Não é possível realizar a leitura automática.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalStateException("Nenhum texto foi encontrado no documento PDF. Documento em branco ou composto apenas por imagens.");
            }
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo PDF", e);
        } finally {
            // Regra de Ouro: Exclusão do arquivo temporário garantida neste finally
            if (arquivoPdf != null && arquivoPdf.exists()) {
                arquivoPdf.delete();
            }
        }
    }
}
