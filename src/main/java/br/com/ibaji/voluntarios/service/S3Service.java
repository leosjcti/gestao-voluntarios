package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.util.ImagemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class S3Service {

    private final S3Client clienteS3;

    @Value("${aws.s3.bucket-name}")
    private String nomeBucket;

    public S3Service(S3Client clienteS3) {
        this.clienteS3 = clienteS3;
    }

    public String enviarArquivo(MultipartFile file, Long volunteerId) {
        try {
            // 1. Processa/Comprime a imagem
            ImagemUtil.ImagemProcessada imagem = ImagemUtil.comprimir(file);

            // 2. Define o nome (se virou JPG, garante a extensão)
            String fileName = file.getOriginalFilename();
            if (imagem.contentType.equals("image/jpeg") && fileName != null && !fileName.toLowerCase().endsWith(".jpg")) {
                fileName = fileName + ".jpg";
            }

            String fileKey = "antecedentes/" + volunteerId + "/" + System.currentTimeMillis() + "-" + fileName;

            // 3. Monta a requisição (CORREÇÃO DO ERRO OBJECT METADATA)
            // No SDK v2, passamos os metadados direto no builder
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(fileKey)
                    .contentType(imagem.contentType)
                    .contentLength(imagem.tamanho) // <--- O Pulo do Gato (sem ObjectMetadata)
                    .build();

            // 4. Envia o stream processado
            clienteS3.putObject(putRequest,
                    RequestBody.fromInputStream(imagem.inputStream, imagem.tamanho));

            return fileKey;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar/enviar arquivo para S3", e);
        }
    }

    public ResponseInputStream<GetObjectResponse> baixarArquivo(String chaveArquivo) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(nomeBucket)
                .key(chaveArquivo)
                .build();

        return clienteS3.getObject(request);
    }
}