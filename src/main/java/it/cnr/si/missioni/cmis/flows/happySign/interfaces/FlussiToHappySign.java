package it.cnr.si.missioni.cmis.flows.happySign.interfaces;

import it.iss.si.dto.happysign.base.File;


import java.util.List;

public interface FlussiToHappySign {
   public String send(String templateName, List<String> signerList, List<String> approvedList, File fileToSign) throws Exception ;

}
