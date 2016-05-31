/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package answeringmachine;
import java.io.File;
import java.nio.file.Files;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class SendEmail {

    final String senderEmail = "iam.dummy.lab3@gmail.com";
    final String senderPassword = "dimedime1";
    final String emailSMTPserver = "smtp.gmail.com";
    final String emailServerPort = "587";
    String receiverEmail = null;
    String emailSubject = null;
    String emailBody = null;
    String caller;
    String filepath;

    public SendEmail(String receiverEmail, String caller, String filepath) {
        this.caller = caller;
        this.receiverEmail = receiverEmail;
        this.emailSubject = "New vioce mail";
        this.emailBody = "Hello, You have new voice mail from " + caller + ". You can find it in the attachments";
        this.filepath = filepath;

        Properties props = new Properties();
        props.put("mail.smtp.user", senderEmail);
        props.put("mail.smtp.host", emailSMTPserver);
        props.put("mail.smtp.port", emailServerPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", emailServerPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        SecurityManager security = System.getSecurityManager();

        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            
            
            File f = new File(filepath);
            byte[] attachmentData = Files.readAllBytes(f.toPath());
            
            Multipart mp = new MimeMultipart();

            MimeBodyPart attachment = new MimeBodyPart();
            attachment.setFileName(f.getName());
            attachment.setContent(attachmentData, "audio/mpeg3");
            mp.addBodyPart(attachment);

            Message msg = new MimeMessage(session);
            //msg.set
            msg.setText(emailBody);
            msg.setSubject(emailSubject);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(receiverEmail));
            msg.setContent(mp);
            Transport.send(msg);
            System.out.println("send successfully");
        } catch (Exception ex) {
            System.err.println("Error occurred while sending.!");
        }

    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
        }
    }

     

}
