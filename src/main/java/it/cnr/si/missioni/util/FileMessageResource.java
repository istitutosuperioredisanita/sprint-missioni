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

package it.cnr.si.missioni.util;

import org.springframework.core.io.ByteArrayResource;

public class FileMessageResource extends ByteArrayResource {

    /**
     * The filename to be associated with the {@link MimeMessage} in the form data.
     */
    private final String filename;

    /**
     * Constructs a new {@link FileMessageResource}.
     *
     * @param byteArray A byte array containing data from a {@link MimeMessage}.
     * @param filename  The filename to be associated with the {@link MimeMessage} in
     *                  the form data.
     */
    public FileMessageResource(final byte[] byteArray, final String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
