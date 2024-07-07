package com.project.userauthservice.services;

import com.project.userauthservice.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendVerificationEmail(String email, String verificationToken) throws MessagingException {
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, false, "utf-8");
        String subject = "Verify Your Email Address";
        String content = "<p>Hi,</p>"
                + "<p>Please click on the link below to verify your email address:</p>"
                + "<p><a href=\"" + Constants.VERIFICATION_LINK + verificationToken + "\">Verify Email</a></p>"
                + "<p>If you didn't request this, you can safely ignore this email.</p>";

        mimeMessage.setContent(content, "text/html");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        javaMailSender.send(mimeMessage);


    }


}
