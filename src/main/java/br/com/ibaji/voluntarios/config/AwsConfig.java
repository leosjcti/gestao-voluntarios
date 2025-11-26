package br.com.ibaji.voluntarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient; // Importação Crucial
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String regiaoAws;

    @Value("${aws.s3.endpoint-url:}")
    private String endpointS3;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Bean
    public S3Client clienteS3() {
        // Cliente HTTP Apache (Solução do conflito)
        var httpClient = ApacheHttpClient.builder().build();

        var builder = S3Client.builder()
                .httpClient(httpClient)
                .region(Region.of(regiaoAws)); // Força a região globalmente

        if (!endpointS3.isEmpty()) {
            // Configuração específica para S3 Compatível (Oracle/LocalStack)
            builder.endpointOverride(URI.create(endpointS3));
            builder.forcePathStyle(true);

            // Credenciais Estáticas
            // IMPORTANTE: O AwsBasicCredentials precisa receber a chave real
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)));
        }

        return builder.build();
    }
//    public S3Client clienteS3() {
//        // 1. A SOLUÇÃO DO ERRO: Criamos o cliente HTTP Apache explicitamente.
//        // Isso elimina a dúvida do SDK sobre qual usar.
//        var httpClient = ApacheHttpClient.builder().build();
//
//        var builder = S3Client.builder()
//                .httpClient(httpClient); // Forçamos o uso do Apache
//
//        // 2. Lógica do LocalStack / Ambiente
//        if (!endpointS3.isEmpty()) {
//            builder.credentialsProvider(StaticCredentialsProvider.create(
//                    AwsBasicCredentials.create("test", "test")));
//            builder.endpointOverride(URI.create(endpointS3));
//            builder.forcePathStyle(true); // Necessário para S3 local
//        } else {
//            builder.credentialsProvider(DefaultCredentialsProvider.create());
//        }
//
//        return builder
//                .region(Region.of(regiaoAws))
//                .build();
//    }
}