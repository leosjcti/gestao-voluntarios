import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMail {
    public static void main(String[] args) {
        String from = "ibaji.gestaoministerial@gmail.com";
        String password = System.getenv("EMAIL_PASSWORD"); // Usar variavel de ambiente

        // Fallback para ler do arquivo .env caso esteja rodando localmente na IDE sem as variaveis setadas
        if (password == null || password.isEmpty()) {
            try {
                password = java.nio.file.Files.lines(java.nio.file.Paths.get(".env"))
                        .filter(line -> line.startsWith("EMAIL_PASSWORD="))
                        .map(line -> line.substring("EMAIL_PASSWORD=".length()).trim())
                        .findFirst()
                        .orElse(null);
            } catch (Exception e) {
                System.out.println("Aviso: Arquivo .env não encontrado.");
            }
        }

        if (password == null || password.isEmpty()) {
            System.err.println("ERRO: Configure a variável EMAIL_PASSWORD no sistema ou no arquivo .env");
            return;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            System.out.println("Connecting and authenticating...");
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", from, password);
            System.out.println("Authentication successful!");
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
