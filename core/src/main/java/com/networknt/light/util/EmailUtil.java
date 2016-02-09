package com.networknt.light.util;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * Created by steve on 06/02/16.
 */
public class EmailUtil {

    static final XLogger logger = XLoggerFactory.getXLogger(EmailUtil.class);

    public static void main(String[] args) throws Exception {
        // test send mail
        sendSignupEmail("www.networknt.com", "stevehu@gmail.com", "abc123");
    }

    // send mail util base on the host. email configuration is host based.
    public static void sendMail(String host, String address, String subject, String content) {
        Map<String, Object> mailConfig = ServiceLocator.getInstance().getJsonMapConfig("email");
        if(mailConfig == null) {
            logger.error("Could not find email.json from externalized config folder");
            return;
        }
        Map<String, Object> config = (Map<String, Object>)mailConfig.get(host);

        String username = (String)config.get("username");
        String password = (String)config.get("password");
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", config.get("mail.smtp.starttls.enable"));
        props.put("mail.smtp.auth", config.get("mail.smtp.auth"));
        props.put("mail.smtp.host", config.get("mail.smtp.host"));
        props.put("mail.smtp.port", config.get("mail.smtp.port"));

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(address));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // This is the email sent out after signup to remind user to activate account
    public static void sendSignupEmail(String host, String address, String code) {
        logger.entry(host, address, code);
        String subject = "Activate your account at " + host;
        String link = "http://" + host + "/api/rs?cmd=";
        String cmd = "{\"category\":\"user\",\"name\":\"activateUser\",\"readOnly\":false,\"data\":{\"email\":\"" + address + "\",\"code\":\"" + code + "\"}}";
        link = link + cmd;
        String content = "Hi,<br>Thanks for registering with us.<br>Please click the following <a href='" + link + "'>link</a> to activate your account.";
        sendMail(host, address, subject, content);;
    }
}
