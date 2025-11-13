package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.service.MailService;
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
public class UtilTestAnnullamentoService {
    private static final Log logger = LogFactory.getLog(UtilTestAnnullamentoService.class);
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
    public StartWorflowDto createUStartWorkflowDto(AnnullamentoOrdineMissione annullamentoOrdineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException{
        StartWorflowDto startInfo= new StartWorflowDto();
        startInfo.setTemplateName("Missioni_doppia_firma_sequenziale");

        //TODO aspettare conferma per impostare 1 sola firma per chi inserisce l'annullamento
        startInfo.addSigner("davide.mirra@iss.it");
        startInfo.addSigner("davide.mirra@iss.it");

        File f = new File();
        f.setFilename(missioniCMISService.parseFilename(modulo.getKey()));
        f.setPdf(getDocumento(modulo));
        if ( allegati!=null && allegati.size()>0){
            for ( StorageObject so:allegati){
                AttachedFile attachedFile = new AttachedFile();
                attachedFile.setAttached(false);
                attachedFile.setContent(getDocumento( so));
                attachedFile.setFilename(so.getPropertyValue(StoragePropertyNames.NAME.value()));
                f.addAttachedFile(attachedFile);
            }
        }

        startInfo.setFileToSign(f);

        return startInfo;
    }

    public void sendMailForAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento, List<String> signers) {
        List<String> destinatari = new ArrayList<>();
        destinatari.add("davide.mirra@iss.it");
        // TODO: Da commentare in locale
//         destinatari.add("martina.damia@iss.it");
//         destinatari.add("simona.fortunato@iss.it");

        StringBuilder testoMail = new StringBuilder();
        testoMail.append("<p>Di seguito gli utenti che, in PRODUZIONE, dovranno firmare l'Annullamento dell'Ordine di Missione: ")
                .append(annullamento.getOrdineMissione().getId())
                .append("<br>Elenco dei firmatari:<br><ul>");

        for (String signer : signers) {
            testoMail.append("<li>").append(signer).append("</li>");
        }
        testoMail.append("</ul>");
        testoMail.append("</p>");

        String testoMailString = testoMail.toString();
        String[] elencoMail = mailService.preparaElencoMail(destinatari);

        if (elencoMail != null && elencoMail.length > 0) {
            mailService.sendEmail(listaFirmatariInProd, testoMailString, false, true, elencoMail);
        }
    }


}
