package br.com.ibaji.voluntarios.service;

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

    public String enviarArquivo(MultipartFile arquivo, Long idVoluntario) {
        try {
            String chaveArquivo = "antecedentes/" + idVoluntario + "/" + System.currentTimeMillis() + "-" + arquivo.getOriginalFilename();

            PutObjectRequest requisicao = PutObjectRequest.builder()
                    .bucket(nomeBucket)
                    .key(chaveArquivo)
                    .contentType(arquivo.getContentType())
                    .build();

            clienteS3.putObject(requisicao, RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            return chaveArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar arquivo para AWS S3", e);
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