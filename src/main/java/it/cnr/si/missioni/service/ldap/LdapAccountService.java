package it.cnr.si.missioni.service.ldap;

import it.cnr.si.missioni.domain.custom.CNRUser;
import it.cnr.si.missioni.model.CNRUserDTO;
import it.cnr.si.missioni.repository.SiperRepository;
import it.cnr.si.missioni.service.security.LdapSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@Profile("!keycloak")
public class LdapAccountService {

    private final static Logger log = LoggerFactory.getLogger(LdapAccountService.class);

    @Autowired
    private SiperRepository siperRepository;

    public CNRUserDTO getSiperAccount () {

        CNRUserDTO cnrUserDto = getAccount();

        Optional<Map> value = siperRepository.getAccountProperties(cnrUserDto.getLogin());

        if (value.isPresent()) {
            Map m = value.get();
            String strutturaAppartenenza = m.getOrDefault("struttura_appartenenza", "").toString();
            String livello = m.getOrDefault("livello_profilo", "").toString();
            String profilo = m.getOrDefault("profilo", "").toString();
            String cittaSede = m.getOrDefault("citta_sede", "").toString();

            cnrUserDto.setStrutturaAppartenenza(strutturaAppartenenza);
            cnrUserDto.setCittaSede(cittaSede);
            cnrUserDto.setLivello(livello);
            cnrUserDto.setProfilo(profilo);
        }

        return cnrUserDto;

    }

    public CNRUserDTO getAccount() {

        UserDetails user = (UserDetails) LdapSecurityUtils.getAuthentication().getPrincipal();

        log.info("user: " + user);

        String matricola;
        String email;
        String departmentNumber;
        String firstName;
        String lastName;

        if (user instanceof CNRUser) {
            CNRUser cnrUser = (CNRUser) user;
            matricola = cnrUser.getMatricola();
            email = cnrUser.getEmail();
            departmentNumber = cnrUser.getDepartmentNumber();
            lastName = cnrUser.getLastName();
            firstName = cnrUser.getFirstName();
        } else {
            email = null;
            matricola = null;
            firstName = null;
            lastName = null;
            departmentNumber = null;
        }

        String username = user.getUsername();
        log.info(username);

        List<String> roles = getRoles(user.getAuthorities());

        log.info(roles.toString());

        return new CNRUserDTO(username, null, matricola, firstName, lastName, email, null, roles, departmentNumber);
    }

    private static List<String> getRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());
    }

}
