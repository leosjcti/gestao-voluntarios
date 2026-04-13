package br.com.ibaji.voluntarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String regiaoAws;

    @Value("${aws.s3.endpoint-url:}")
    private String endpointS3;

    // O Java vai tentar ler do application.properties
    // Se falhar, ele tenta ler da variável de ambiente AWS_ACCESS_KEY (graças ao Spring Relaxed Binding)
    @Value("${aws.access-key:}")
    private String accessKey;

    @Value("${aws.secret-key:}")
    private String secretKey;

    @Bean
    public S3Client clienteS3() {
        System.out.println(">>> LENDO DADOS DE ACESSO DO S3 VIA VARIAVEIS (APPLICATION.PROPERTIES / .ENV) <<<");

        return S3Client.builder()
                .httpClient(ApacheHttpClient.builder().build())
                .region(Region.of(regiaoAws))
                .endpointOverride(endpointS3 != null && !endpointS3.isEmpty() ? URI.create(endpointS3) : null)
                .forcePathStyle(true) // Obrigatório para Oracle Cloud
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}