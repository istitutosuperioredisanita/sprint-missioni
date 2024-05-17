package it.cnr.si.missioni.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "admin")
public class ExternalUserAdminService {
    List<String> usernames;

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
    public Boolean isExternalUserAdmin( String username) {

        return Optional.ofNullable(usernames).orElse(new ArrayList<String>()).stream().filter(s -> s.equals(username)).findFirst().isPresent();
    }
}
