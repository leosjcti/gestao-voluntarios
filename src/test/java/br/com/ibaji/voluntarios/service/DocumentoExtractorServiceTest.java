package br.com.ibaji.voluntarios.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoExtractorServiceTest {

    @Test
    void deveDeletarArquivoTemporarioAposExtracao() throws IOException {
        DocumentoExtractorService service = new DocumentoExtractorService();
        File tempFile = File.createTempFile("teste-pdf", ".pdf");
        
        // Cria um PDF mínimo para não estourar exception
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(tempFile);
        }

        try {
            service.extrairTexto(tempFile);
        } catch (IllegalStateException e) {
            // Esperado falhar porque o PDF criado acima não tem texto
        }

        Path path = tempFile.toPath();
        assertFalse(Files.exists(path), "O arquivo temporário deve ser deletado do disco no bloco finally");
    }
}
