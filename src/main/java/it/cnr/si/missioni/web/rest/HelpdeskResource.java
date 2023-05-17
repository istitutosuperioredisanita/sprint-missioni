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

package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.domain.custom.ExternalProblem;
import it.cnr.si.missioni.service.HelpdeskService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RolesAllowed({AuthoritiesConstants.USER})
@RestController
@RequestMapping("/api")
public class HelpdeskResource {
    private final Logger log = LoggerFactory.getLogger(HelpdeskResource.class);
    @Autowired
    private HelpdeskService helpdeskService;

    @RequestMapping(value = "/rest/helpdesk/sendWithAttachment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity sendWithAttachment(HttpServletRequest req, @RequestParam("file") MultipartFile uploadedMultipartFile) {
        log.debug("HelpdeskResource:send");
        ExternalProblem hd = new ExternalProblem();
        Long id = null;
        if (StringUtils.hasLength(req.getParameter("idSegnalazione"))) {
            id = Long.valueOf(req.getParameter("idSegnalazione"));
            hd.setIdSegnalazione(id);
            hd.setNota(req.getParameter("nota"));
        } else {
            hd.setTitolo(req.getParameter("titolo"));
            hd.setDescrizione(req.getParameter("descrizione"));
            hd.setCategoria(Integer.valueOf(req.getParameter("categoria")));
            hd.setCategoriaDescrizione(req.getParameter("categoriaDescrizione"));
        }
        id = helpdeskService.newProblem(hd);

        helpdeskService.addAttachments(id, uploadedMultipartFile);

        return JSONResponseEntity.ok();
    }

    @RequestMapping(value = "/rest/helpdesk/sendWithoutAttachment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendWithoutAttachment(HttpServletRequest req, @RequestBody ExternalProblem hdDataModel) {
        log.debug("HelpdeskResource:send");
        helpdeskService.newProblem(hdDataModel);
        return JSONResponseEntity.ok();
    }
}
