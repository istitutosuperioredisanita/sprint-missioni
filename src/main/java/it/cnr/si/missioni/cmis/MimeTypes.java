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

package it.cnr.si.missioni.cmis;

public enum MimeTypes {
    BMP("image/bmp"),
    XLS("application/vnd.ms-excel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JPG("image/jpeg"),
    JPEG("image/pjpeg"),
    TIFF("image/tiff"),
    PNG("image/png"),
    GZ("application/x-gzip"),
    TAR("application/x-tar"),
    ZIP("application/zip"),
    ZIP_2("application/x-compressed-zip"),
    HTML("text/html"),
    XHTML("text/xhtml"),
    TEXT("text/plain"),
    JAVASCRIPT("text/javascript"),
    APP_XML("application/xml"),
    XML("text/xml"),
    PDF("application/pdf"),
    ATOM("application/atom+xml"),
    ATOMFEED("application/atom+xml;type=feed"),
    ATOMENTRY("application/atom+xml;type=entry"),
    FORMDATA("multipart/form-data"),
    JSON("application/json");

    private final String mimetype;

    MimeTypes(String mimetype) {
        this.mimetype = mimetype;
    }

    public String mimetype() {
        return mimetype;
    }
}