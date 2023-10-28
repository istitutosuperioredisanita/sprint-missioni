package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.cmis.CMISOrdineMissione;
import it.cnr.si.missioni.cmis.flows.happySign.dto.StartWorflowDto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;

import java.io.IOException;
import java.util.List;

public interface FlussiMissione<T> extends FlussiToHappySign{

     Boolean isFlowToSend(T ordineMissione);

     StartWorflowDto createUStartWorfloDto(T ordineMissione, StorageObject modulo, List<StorageObject> allegati) throws IOException;


}