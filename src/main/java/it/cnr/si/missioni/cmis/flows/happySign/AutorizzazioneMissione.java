package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;

public interface AutorizzazioneMissione extends FlussiToHappySign{

    public Boolean isFlowToSend(OrdineMissione ordineMissione);

    public UploadToComplexRequest createUploadComplexrequest(OrdineMissione ordineMissione);
}
