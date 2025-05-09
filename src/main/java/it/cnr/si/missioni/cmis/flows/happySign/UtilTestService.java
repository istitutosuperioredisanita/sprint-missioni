package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.cmis.flows.happySign.interfaces.AutorizzazioneMissione;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OggettoBulkXmlTransient;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.service.MailService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.iss.si.dto.happysign.base.AttachedFile;
import it.iss.si.dto.happysign.base.File;
import it.iss.si.service.HappySignService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "flows", name = "test", havingValue = "true")
public class UtilTestService {
    private static final Log logger = LogFactory.getLog(UtilTestService.class);
    @Autowired
    protected HappySignService happySignService;

    @Autowired
    protected MissioniCMISService missioniCMISService;

    @Autowired(required = false)
    private MailService mailService;
    @Value("${spring.mail.messages.listaFirmatariInProd.oggetto}")
    private String listaFirmatariInProd;

    public byte[] getDocumento(StorageObject storageObject) throws IOException {
        return IOUtils.toByteArray(missioniCMISService.getResource(storageObject));
    }

    public StartWorflowDto createStartWorkflowDto(OrdineMissione ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException {
        StartWorflowDto startInfo = new StartWorflowDto();
        startInfo.setTemplateName("duilio_app");

        //TODO aspettare conferma per impostare 1 sola firma per chi inserisce l'ordine
        startInfo.addSigner(ordineMissione.getUidInsert());
        startInfo.addSigner(ordineMissione.getUidInsert());

        File f = new File();
        f.setFilename(missioniCMISService.parseFilename(modulo.getKey()));
        f.setPdf(getDocumento(modulo));
        if (allegati != null && allegati.size() > 0) {
            for (StorageObject so : allegati) {
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setAttached(false);
                attachedFile.setContent(getDocumento(so));
                attachedFile.setFilename(so.getPropertyValue(StoragePropertyNames.NAME.value()));
                f.addAttachedFile(attachedFile);
            }
        }

        startInfo.setFileToSign(f);

        return startInfo;
    }

    public static void showSigned(StartWorflowDto startWorflowDto) {
        logger.info("firmatari missione in produzione");
        List<String> signers = startWorflowDto.getSigners();
        if (signers == null || signers.isEmpty()) {
            logger.error("non ci sono firmatari");
        } else {
            logger.info("firmatari presenti: \n\n");
            for (String signer :
                    signers) {
                logger.info(signer + "\n");

            }
        }
    }



    public void sendMailForOrdineMissione(OrdineMissione ordineMissione, List<String> signers, AutorizzazioneMissione autorizzazione) {
        List<String> destinatari = new ArrayList<>();
        destinatari.add("davide.mirra@iss.it");
        // TODO: Da commentare in locale
//         destinatari.add("martina.damia@iss.it");
//         destinatari.add("simona.fortunato@iss.it");

        StringBuilder testoMail = new StringBuilder();
        testoMail.append("<p>Di seguito gli utenti che, in PRODUZIONE, dovranno firmare l'Ordine di Missione: ")
                .append(ordineMissione.getId())
                .append("<br>Elenco dei firmatari:<br><ul>");

        for (String signer : signers) {
            testoMail.append("<li>").append(signer).append("</li>");
        }
        testoMail.append("</ul>");

        // Determina la classe utilizzata per il metodo createStartWorkflowDto
        String classeImplementata = getClasseImplementazione(autorizzazione);
        testoMail.append("<br><p>Il tipo di missione inviata in firma è: ").append("<b>").append(classeImplementata).append("</b>")
                .append(".</p>");

        testoMail.append("</p>");

        String testoMailString = testoMail.toString();
        String[] elencoMail = mailService.preparaElencoMail(destinatari);

        if (elencoMail != null && elencoMail.length > 0) {
            mailService.sendEmail(listaFirmatariInProd, testoMailString, false, true, elencoMail);
        }
    }

    private String getClasseImplementazione(AutorizzazioneMissione autorizzazione) {

        String className = autorizzazione.getClass().getSimpleName();
        if (className.equals(Costanti.CLASS_AUTORIZZ_MISS_GAE)){
            return Costanti.TIPO_MISS_GAE;
        } else if(className.equals(Costanti.CLASS_AUTORIZZ_MISS_DIR_GAE)) {
            return  Costanti.TIPO_MISS_DIR_GAE;
        }
        else {
            return className;
        }

    }




}
