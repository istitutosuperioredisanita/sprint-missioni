/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.cmis.acl;

import java.util.HashMap;
import java.util.Map;


public class Permission {
    private String userName;
    private ACLType role;

    protected Permission(String userName, ACLType role) {
        super();
        this.userName = userName;
        this.role = role;
    }

    public static Permission construct(String userName, ACLType role) {
        return new Permission(userName, role);
    }

    public static Map<String, ACLType> convert(Permission... permissions) {
        Map<String, ACLType> result = new HashMap<String, ACLType>();
        for (Permission permission : permissions) {
            result.put(permission.getUserName(), permission.getRole());
        }
        return result;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ACLType getRole() {
        return role;
    }

    public void setRole(ACLType role) {
        this.role = role;
    }

}
