package it.cnr.si.missioni.cmis.flows.happySign.interfaces;

import it.cnr.si.missioni.cmis.flows.happySign.dto.StartSignatureFlowDto;

public interface FlussiToHappySign {

   String send(StartSignatureFlowDto startInfo);

}