package it.cnr.si.missioni.service.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ACEService {
    public UserInfoDto getUtenteAdmin(){
       InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/UserInfo.json");
        try {
            return new ObjectMapper().readValue(is, UserInfoDto.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle nazioni per lo showcase." + Utility.getMessageException(e));
        }
    }
}
