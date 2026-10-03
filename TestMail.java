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
        String password = "ysxhmdqmwippmzyh";

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
