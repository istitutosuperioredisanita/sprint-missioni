/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.service;


import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.User;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Profile("!showcase")
@Service("MailService")
public class MailService {
    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private JavaMailSenderImpl javaMailSender;

    /**
     * System default email address that sends the e-mails errors.
     */
    private List<String> mailToError = new ArrayList<String>();

    /**
     * System default email address that sends the e-mails errors.
     */
    private String from;

    @Autowired
    private AccountService accountService;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
        String s = env.getProperty("spring.mail.send.error.to");
        if (s == null)
            s = "ciro.salvio@iss.it";
        this.mailToError = Arrays.asList(s.split(","));
    }

    public String[] prepareTo(List<UsersSpecial> lista) {
        List<String> listaEmail = preparaListaMail(lista);
        return preparaElencoMail(listaEmail);
    }

//no duplicati nella group-list dei destinatari
    public List<String> preparaListaMail(List<UsersSpecial> lista) {
        Set<String> emailSet = new HashSet<>();
        List<String> listaEmail = new ArrayList<>();

        for (UsersSpecial user : lista) {
            String mail = user.getUid();
            if (!StringUtils.isEmpty(mail) && emailSet.add(mail)) {
                listaEmail.add(mail);
            }
        }
        return listaEmail;
    }

    public String[] preparaElencoMail(List<String> listaEmail) {
        if (!listaEmail.isEmpty()) {
            String[] elencoMail = new String[listaEmail.size()];
            elencoMail = listaEmail.toArray(elencoMail);
            return elencoMail;
        }
        return null;
    }

    private void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isMultipart, boolean isHtml, String... to) {
        log.info("Send e-mail[to '{}' with subject '{}'", to, subject);
        if (to != null) {
            // Prepare message using a Spring helper
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setText(content, isHtml);
                if (multipartFile != null && !multipartFile.isEmpty())
                    message.addAttachment(multipartFile.getOriginalFilename(), new ByteArrayResource(multipartFile.getBytes()), multipartFile.getContentType());
                if (!isDevProfile()) {
                    javaMailSender.send(mimeMessage);
                }
                log.debug("Sent e-mail to User '{}'", to);
            } catch (Exception e) {
                log.error("E-mail could not be sent to user '{}', exception is: {}", to, e);
                throw new AwesomeException("Errore nell'invio dell'e-mail: " + Utility.getMessageException(e), e);
            }
        }
    }

    public void sendEmail(String subject, String content, boolean isMultipart, boolean isHtml, String... to) {
        // Remove duplicate email addresses
        Set<String> uniqueRecipients = new HashSet<>(Arrays.asList(to));
        String[] uniqueToArray = uniqueRecipients.toArray(new String[0]);

        sendEmail(subject, content, null, isMultipart, isHtml, uniqueToArray);
    }

    public void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isHtml, String... to) {
        sendEmail(subject, content, multipartFile, true, isHtml, to);
    }

    public void sendEmailError(String subject, String content, boolean isMultipart, boolean isHtml) {
        if (!isDevProfile()) {
            for (String emailTo : mailToError)
                sendEmail(subject, content, isMultipart, isHtml, emailTo);

        }
    }

    private boolean isDevProfile() {
        return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT) || env.acceptsProfiles(Costanti.SPRING_PROFILE_SHOWCASE);
    }

    @Async
    public void sendCreationEmail(User user, String baseUrl) {
        log.debug("Sending creation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        throw new UnsupportedOperationException("creationEmail");
    }
}