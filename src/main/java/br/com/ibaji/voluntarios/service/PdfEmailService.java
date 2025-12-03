package br.com.ibaji.voluntarios.service;

import br.com.ibaji.voluntarios.model.Voluntario;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfEmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public PdfEmailService(TemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    // Método Assíncrono para não travar o cadastro do usuário
    @Async
    public void gerarEEnviarTermo(Voluntario voluntario) {
        try {
            // 1. Gerar PDF
            byte[] pdfBytes = gerarPdfTermo(voluntario);

            // 2. Enviar E-mail
            enviarEmailComAnexo(voluntario, pdfBytes);

            System.out.println("E-mail com termo enviado para: " + voluntario.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao enviar termo por e-mail: " + e.getMessage());
        }
    }

    private byte[] gerarPdfTermo(Voluntario voluntario) throws Exception {
        Context context = new Context();
        context.setVariable("voluntario", voluntario);

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        context.setVariable("dataExtenso", hoje.format(fmt));
        context.setVariable("dataInicio", hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String html = templateEngine.process("termo-voluntariado", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // --- CORREÇÃO: DIZENDO ONDE GRAVAR ---
            builder.toStream(os);

            builder.run();
            return os.toByteArray();
        }
    }

    private void enviarEmailComAnexo(Voluntario voluntario, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // true = multipart (para aceitar anexo)
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(remetente);
        helper.setTo(voluntario.getEmail());
        helper.setSubject("Seu Termo de Voluntariado - IBAJI");
        helper.setText("Olá " + voluntario.getNomeCompleto() + ",\n\n" +
                "Bem-vindo ao time de voluntários!\n" +
                "Segue em anexo o seu Termo de Voluntariado preenchido.\n\n" +
                "Deus abençoe,\nIgreja Batista no Jardim das Indústrias.");

        // Adiciona o PDF
        helper.addAttachment("Termo_Voluntariado.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}