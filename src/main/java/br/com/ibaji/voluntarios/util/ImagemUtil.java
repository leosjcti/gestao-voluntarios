package br.com.ibaji.voluntarios.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImagemUtil {

    // Classe auxiliar para transportar o resultado
    public static class ImagemProcessada {
        public InputStream inputStream;
        public long tamanho;
        public String contentType;

        public ImagemProcessada(InputStream inputStream, long tamanho, String contentType) {
            this.inputStream = inputStream;
            this.tamanho = tamanho;
            this.contentType = contentType;
        }
    }

    public static ImagemProcessada comprimir(MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        // Se não for imagem (ex: PDF), retorna o original sem mexer
        if (contentType == null || !contentType.startsWith("image")) {
            return new ImagemProcessada(file.getInputStream(), file.getSize(), contentType);
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            // Caso não consiga ler como imagem, devolve original
            return new ImagemProcessada(file.getInputStream(), file.getSize(), contentType);
        }

        // Configuração de Redimensionamento
        int targetWidth = 1200; // Largura máxima
        int originalWidth = originalImage.getWidth();

        // Se a imagem já for pequena, não faz nada
        if (originalWidth <= targetWidth) {
            return new ImagemProcessada(file.getInputStream(), file.getSize(), contentType);
        }

        int targetHeight = (int) (originalImage.getHeight() * ((double) targetWidth / originalWidth));

        // Cria nova imagem redimensionada
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // Melhora a qualidade do redimensionamento
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        // Converte para JPG (comprime bem)
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", os);

        byte[] bytes = os.toByteArray();

        return new ImagemProcessada(new ByteArrayInputStream(bytes), bytes.length, "image/jpeg");
    }
}