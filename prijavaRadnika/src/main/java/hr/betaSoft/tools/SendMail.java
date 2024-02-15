package hr.betaSoft.tools;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.secService.UserService;
import hr.betaSoft.service.EmployeeService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {

    public static void sendMail(String recipient, String subject, String text) {

        final String username = "betasoft@abel.hr";
        final String password = "Betasoft1503";
        final String host = "mail.abel.hr";
        final int port = 465;

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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            System.out.println("Success");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
