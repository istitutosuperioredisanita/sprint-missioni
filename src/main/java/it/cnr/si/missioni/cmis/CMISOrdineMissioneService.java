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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.awesome.exception.TaskIdNonTrovatoException;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.service.*;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.*;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.service.SecurityService;
import it.cnr.si.spring.storage.StorageDriver;
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public interface CMISOrdineMissioneService {


    public CMISOrdineMissione create(OrdineMissione ordineMissione) throws ComponentException;

    public CMISOrdineMissione create(OrdineMissione ordineMissione, Integer annoGestione) throws ComponentException;


    @Transactional(readOnly = true)
    public StorageObject salvaStampaOrdineMissioneSuCMIS(byte[] stampa, OrdineMissione ordineMissione);



    public String createFolderOrdineMissione(OrdineMissione ordineMissione) ;



    public StorageObject salvaStampaAnnullamentoOrdineMissioneSuCMIS(
            byte[] stampa, AnnullamentoOrdineMissione annullamento) ;


    public Map<String, Object> createMetadataForFileOrdineMissione(String currentLogin, CMISOrdineMissione cmisOrdineMissione);

    public void avviaFlusso(AnnullamentoOrdineMissione annullamento);

    public void avviaFlusso(OrdineMissione ordineMissione);



    public StorageObject getStorageObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException;


    public InputStream getStreamOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException ;

    public InputStream getStreamOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException;

    public StorageObject getObjectOrdineMissione(OrdineMissione ordineMissione) throws ComponentException ;

    public StorageObject getObjectAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException;

    public StorageObject getObjectAnticipoOrdineMissione(OrdineMissioneAnticipo anticipo) throws ComponentException;

    public String getNodeRefOrdineMissione(OrdineMissione ordineMissione) throws ComponentException ;

    public String getNodeRefAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException;

    public StorageObject getStorageAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento) throws ComponentException ;

    public StorageObject getStorageOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException;

    public StorageObject getStorageOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException;

    public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException;
    public String getNodeRefOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria, Boolean erroreSeNonTrovato) throws ComponentException;

    public String getNodeRefOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo);
    public StorageObject recuperoFolderOrdineMissione(OrdineMissione ordineMissione);

    public void annullaFlusso(OrdineMissione ordineMissione);


    public Map<String, Object> createMetadataForFileOrdineMissioneAnticipo(String currentLogin, OrdineMissioneAnticipo anticipo);

    public Map<String, Object> createMetadataForFileOrdineMissioneAutoPropria(String currentLogin, OrdineMissioneAutoPropria autoPropria);

    public Map<String, Object> createMetadataForFileAnnullamentoOrdineMissione(String currentLogin, AnnullamentoOrdineMissione annullamento);

    @Transactional(readOnly = true)
    public Map<String, Object> createMetadataForFileOrdineMissioneAllegati(String currentLogin, String fileName, String tipoAllegato);

    @Transactional(readOnly = true)
    public StorageObject salvaStampaAutoPropriaSuCMIS(String currentLogin, byte[] stampa,
                                                      OrdineMissioneAutoPropria ordineMissioneAutoPropria) throws ComponentException ;

    @Transactional(readOnly = true)
    public StorageObject salvaStampaAnticipoSuCMIS(String currentLogin, byte[] stampa,
                                                   OrdineMissioneAnticipo ordineMissioneAnticipo) throws ComponentException ;

    public List<CMISFileAttachment> getAttachmentsOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione) ;

    public List<CMISFileAttachment> getAttachmentsAnticipo(OrdineMissione ordineMissione, Long idAnticipo) ;

    public List<StorageObject> getAttachmentsAnticipo(OrdineMissione ordineMissione) ;

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione);

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione, Boolean recuperoFileEliminati);
    public CMISFileAttachment uploadAttachmentAnticipo(OrdineMissione ordineMissione, Long idAnticipo, InputStream inputStream, String name, MimeTypes mimeTypes) ;

    public CMISFileAttachment uploadAttachmentOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione, InputStream inputStream, String name, MimeTypes mimeTypes);



    public Map<String, byte[]> getFileAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento);

    public Map<String, byte[]> getFileOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo);

    public Map<String, byte[]> getFileOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria);

    public List<StorageObject> getAllDocumentsOrdineMissione(OrdineMissione missione) throws ComponentException;

}
