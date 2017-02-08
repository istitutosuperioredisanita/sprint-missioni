package it.cnr.si.missioni.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.util.Utility;

@Service("MailService")
public class MailService {
    private final Logger log = LoggerFactory.getLogger(MailService.class);
    
    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    /**
     * System default email address that sends the e-mails errors.
     */
	private List<String> mailToError = new ArrayList<String>();

    /**
     * System default email address that sends the e-mails errors.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
    	String s = env.getProperty("spring.mail.send.error.to");
        if (s==null) 
        	s="gianfranco.gasparro@cnr.it";
       	this.mailToError = Arrays.asList(s);
    }
    
    private void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isMultipart, boolean isHtml, String... to) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}', content={}",
                isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (multipartFile!=null && !multipartFile.isEmpty())
            	message.addAttachment(multipartFile.getOriginalFilename(), new ByteArrayResource(multipartFile.getBytes()), multipartFile.getContentType());
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.error("E-mail could not be sent to user '{}', exception is: {}", to, e);
            throw new ComponentException("Errore nell'invio dell'e-mail: "+Utility.getMessageException(e),e);
        }
    }

    public void sendEmail(String subject, String content, boolean isMultipart, boolean isHtml, String... to) {
    	sendEmail(subject, content, null, isMultipart, isHtml, to);
    }

    public void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isHtml, String... to) {
    	sendEmail(subject, content, multipartFile, true, isHtml, to);
    }

    public void sendEmailError(String subject, String content, boolean isMultipart, boolean isHtml) {
       	for (String emailTo : mailToError)
           	sendEmail(subject, content, isMultipart, isHtml, emailTo);
    }
}