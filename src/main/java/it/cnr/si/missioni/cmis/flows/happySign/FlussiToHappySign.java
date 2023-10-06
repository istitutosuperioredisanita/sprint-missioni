package it.cnr.si.missioni.cmis.flows.happySign;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.iss.si.dto.happysign.request.UploadToComplexRequest;
import it.iss.si.dto.happysign.response.UploadToComplexResponse;

public interface FlussiToHappySign {
   UploadToComplexResponse send(UploadToComplexRequest request);

}
