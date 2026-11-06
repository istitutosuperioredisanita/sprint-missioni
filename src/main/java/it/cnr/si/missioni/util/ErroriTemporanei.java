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
 */

package it.cnr.si.missioni.util;

import feign.FeignException;
import feign.RetryableException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public final class ErroriTemporanei {

    private ErroriTemporanei() {
    }

    public static boolean isTemporaneo(Throwable e) {
        Throwable current = e;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof ConnectException || current instanceof RetryableException) return true;
            if (current instanceof FeignException && (((FeignException) current).status() == 404 || ((FeignException) current).status() >= 500)) return true;
            if (current instanceof org.springframework.dao.OptimisticLockingFailureException ||
                    current instanceof jakarta.persistence.OptimisticLockException ||
                    current instanceof org.springframework.dao.PessimisticLockingFailureException ||
                    current instanceof jakarta.persistence.PessimisticLockException ||
                    current instanceof org.springframework.dao.CannotAcquireLockException) return true;
            if (current.getMessage() != null && current.getMessage().contains("in modifica. Ripetere l'operazione")) return true;
            // documento firmato non ancora presente sul documentale
            if (current.getMessage() != null && current.getMessage().contains("Non esistono documenti collegati")) return true;
            current = current.getCause();
        }
        return false;
    }
}
