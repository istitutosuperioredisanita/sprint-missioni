package it.cnr.si.missioni.service.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Iterator;

@Service
@Profile("showcase")
public class ACEService {
    public UserInfoDto getUtenteAdmin(String user){
       InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/UserInfo.json");
        try {
            Users users = new ObjectMapper().readValue(is, Users.class);
            for (Iterator<UserInfoDto> iteratorUsers = users.getUsers().iterator(); iteratorUsers.hasNext();){
                UserInfoDto userInfoDto = iteratorUsers.next();
                if (userInfoDto.getUid() != null && userInfoDto.getUid().equalsIgnoreCase(user)){
                    return userInfoDto;
                }
            }

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON degli utenti per lo showcase." + Utility.getMessageException(e));
        }
        return null;
    }
}
