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

package it.cnr.si.missioni.awesome.exception;

import it.cnr.si.missioni.util.CodiciErrore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AwesomeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6063577040643273974L;

    private final int code;

    public AwesomeException(int code) {
        this.code = code;
    }

    public AwesomeException(String message) {
        super(message);
        this.code = CodiciErrore.ERRGEN;
    }

    public AwesomeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AwesomeException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : (CodiciErrore.text[code] + " [" + code + "]");
    }

    public ResponseEntity<?> getResponse() {

        ResponseEntity<?> response = null;
        if (this.getCode() == CodiciErrore.OK)
            response = new ResponseEntity<>(HttpStatus.CREATED);
        else
            response = new ResponseEntity<String>(this.getMessage(), HttpStatus.BAD_REQUEST);
        return response;
    }
}
