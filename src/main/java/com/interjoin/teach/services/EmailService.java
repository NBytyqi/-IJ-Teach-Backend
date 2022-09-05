package com.interjoin.teach.services;

import com.interjoin.teach.dtos.EmailDTO;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SendGrid sendGridClient;
    @Value("${spring.sendgrid.reply-to}")
    private String replyToEmail;

    @Value("${spring.sendgrid.default-email}")
    private String defaultEmailFrom;

    public void sendEmail(EmailDTO emailDTO) {
        Mail mail = configureMail(emailDTO.getTemplateId());
        Map<String, String> templateDynamicData = emailDTO.getTemplateKeys();

        Personalization personalization = new Personalization();
        if(Optional.ofNullable(templateDynamicData).isPresent()) {
            for(String key : templateDynamicData.keySet()) {
                personalization.addDynamicTemplateData(key, templateDynamicData.get(key));
            }
        }

        personalization.addTo(new Email(emailDTO.getToEmail()));
        mail.addPersonalization(personalization);
        sendText(mail);
    }

    public void sendText(Mail mail) {
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGridClient.api(request);
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }
    }

    private Mail configureMail(String templateId) {
        Email from = new Email(defaultEmailFrom, "InterJoinTeach");

        Mail mail = new Mail();

        mail.setFrom(from);
        mail.setReplyTo(new Email(replyToEmail, "InterJoinTeach"));
        mail.setTemplateId(templateId);
        return mail;
    }
}