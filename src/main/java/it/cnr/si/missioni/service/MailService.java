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
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;

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
        if (s==null) 
        	s="gianfranco.gasparro@cnr.it";
       	this.mailToError = Arrays.asList(s.split(","));
    }
    
	public String[] prepareTo(List<UsersSpecial> lista) {
		List<String> listaEmail = preparaListaMail(lista);
		return preparaElencoMail(listaEmail);
	}

	public List<String> preparaListaMail(List<UsersSpecial> lista) {
		List<String> listaEmail = new ArrayList<>();
		for (int i = 0; i < lista.size(); i++){
			UsersSpecial user = lista.get(i);
			String mail = accountService.getEmail(user.getUid());
			if (!StringUtils.isEmpty(mail)){
				listaEmail.add(mail);
			}
		}
		return listaEmail;
	}

	public String[] preparaElencoMail(List<String> listaEmail) {
		if (!listaEmail.isEmpty()){
			String[] elencoMail = new String[listaEmail.size()];
			elencoMail = listaEmail.toArray(elencoMail); 
			return elencoMail;
		}
		return null;
	}

    private void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isMultipart, boolean isHtml, String... to) {
        log.info("Send e-mail[to '{}' with subject '{}'", to, subject);
        if (to != null){
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
				if (!isDevProfile()){
					javaMailSender.send(mimeMessage);
				}
                log.debug("Sent e-mail to User '{}'", to);
            } catch (Exception e) {
                log.error("E-mail could not be sent to user '{}', exception is: {}", to, e);
                throw new ComponentException("Errore nell'invio dell'e-mail: "+Utility.getMessageException(e),e);
            }
        }
    }

    public void sendEmail(String subject, String content, boolean isMultipart, boolean isHtml, String... to) {
    	sendEmail(subject, content, null, isMultipart, isHtml, to);
    }

    public void sendEmail(String subject, String content, MultipartFile multipartFile, boolean isHtml, String... to) {
    	sendEmail(subject, content, multipartFile, true, isHtml, to);
    }

    public void sendEmailError(String subject, String content, boolean isMultipart, boolean isHtml) {
    	if (!isDevProfile()){
        	for (String emailTo : mailToError)
           		sendEmail(subject, content, isMultipart, isHtml, emailTo);
    		
    	}
    }
	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT) || env.acceptsProfiles(Costanti.SPRING_PROFILE_SHOWCASE)) {
   			return true;
   		}
   		return false;
	}
}