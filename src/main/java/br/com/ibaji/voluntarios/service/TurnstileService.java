package br.com.ibaji.voluntarios.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TurnstileService {

    @Value("${cloudflare.turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verificarToken(String token, String ipCliente) {
        System.out.println("Iniciando verificação de Turnstile...");
        System.out.println("Token recebido (primeiros 15 caracteres): " + (token != null && token.length() > 15 ? token.substring(0, 15) + "..." : token));
        System.out.println("IP do cliente: " + ipCliente);

        if (token == null || token.trim().isEmpty()) {
            System.err.println("Token do Turnstile está vazio!");
            return false;
        }

        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", secretKey);
        map.add("response", token);
        
        // Evitar enviar remoteip se for localhost ou IP privado, pois Cloudflare pode rejeitar
        if (ipCliente != null && !ipCliente.equals("127.0.0.1") && !ipCliente.equals("0:0:0:0:0:0:0:1") && !ipCliente.startsWith("172.") && !ipCliente.startsWith("192.168.")) {
            map.add("remoteip", ipCliente);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();
            System.out.println("Resposta da Cloudflare: " + body);
            
            if (body != null) {
                Object success = body.get("success");
                if (success instanceof Boolean) {
                    return (Boolean) success;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao validar Turnstile: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
