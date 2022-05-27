package it.cnr.si.missioni.service.showcase;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;

import java.util.ArrayList;
import java.util.List;

public class Users {
    @JsonProperty("users")
    private List<UserInfoDto> users = new ArrayList<UserInfoDto>();

    public List<UserInfoDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfoDto> users) {
        this.users = users;
    }
}
