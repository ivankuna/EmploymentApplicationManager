package hr.betaSoft.tools;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {

    public static void sendMail(Integer kontrola) {
        final String username = "betasoft@abel.hr";
        final String password = "Betasoft1503";
        final String host = "mail.abel.hr";
        final int port = 465;
        final String receiverEmail = "sinisa.kuna@yahoo.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("Testiranje slanje mail-a");

//            ako će se dodavati u body html
//            String htmlBody = "<html><body><h1>Testiranje slanje mail-a</h1><p>ovo je test slanja mail-a abel - java</p></body></html>";
//            message.setContent(htmlBody, "text/html");

            String plainTextBody = "Ovo je test slanja mail-a abel - java broj # " + kontrola;
            message.setText(plainTextBody);

            Transport.send(message);
            System.out.println("Success");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
