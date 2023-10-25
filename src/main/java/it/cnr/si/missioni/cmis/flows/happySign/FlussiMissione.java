package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.CMISOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;

import java.io.IOException;

public interface FlussiMissione<T> extends FlussiToHappySign{

    public Boolean isFlowToSend(T ordineMissione);

    public UploadToComplexRequest createUploadComplexrequest(T ordineMissione, StorageObject moduloOrdineMissione) throws IOException;


}