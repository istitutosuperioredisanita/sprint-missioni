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

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.spring.storage.StorageObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


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

    public String getNodeRefOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi, Boolean erroreSeNonTrovato) throws ComponentException;

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

    @Transactional(readOnly = true)
    public StorageObject salvaStampaTaxiSuCMIS(String currentLogin, byte[] stampa,
                                                   OrdineMissioneTaxi ordineMissioneTaxi) throws ComponentException ;

    public List<CMISFileAttachment> getAttachmentsOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione) ;

    public List<CMISFileAttachment> getAttachmentsAnticipo(OrdineMissione ordineMissione, Long idAnticipo) ;
    public List<CMISFileAttachment> getAttachmentsTaxi(OrdineMissione ordineMissione, Long idTaxi) ;
    public List<StorageObject> getAttachmentsTaxi(OrdineMissione ordineMissione) ;

    public List<StorageObject> getAttachmentsAnticipo(OrdineMissione ordineMissione) ;

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione);

    public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione ordineMissione, Boolean recuperoFileEliminati);
    public CMISFileAttachment uploadAttachmentAnticipo(OrdineMissione ordineMissione, Long idAnticipo, InputStream inputStream, String name, MimeTypes mimeTypes) ;
    public CMISFileAttachment uploadAttachmentTaxi(OrdineMissione ordineMissione, Long idTaxi, InputStream inputStream, String name, MimeTypes mimeTypes) ;

    public CMISFileAttachment uploadAttachmentOrdineMissione(OrdineMissione ordineMissione, Long idOrdineMissione, InputStream inputStream, String name, MimeTypes mimeTypes);



    public Map<String, byte[]> getFileAnnullamentoOrdineMissione(AnnullamentoOrdineMissione annullamento);

    public Map<String, byte[]> getFileOrdineMissioneAnticipo(OrdineMissioneAnticipo ordineMissioneAnticipo);
    public Map<String, byte[]> getFileOrdineMissioneTaxi(OrdineMissioneTaxi ordineMissioneTaxi);

    public Map<String, byte[]> getFileOrdineMissioneAutoPropria(OrdineMissioneAutoPropria ordineMissioneAutoPropria);

    public List<StorageObject> getAllDocumentsOrdineMissione(OrdineMissione missione) throws ComponentException;

}