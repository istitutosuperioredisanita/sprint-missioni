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
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.DatiFlusso;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.spring.storage.StorageObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface CMISRimborsoMissioneService {
    public static final String PROPERTY_TIPOLOGIA_DOC = "wfcnr:tipologiaDOC";
    public static final String PROPERTY_TIPOLOGIA_DOC_SPECIFICA = "wfcnr:tipologiaDocSpecifica";
    public static final String PROPERTY_TIPOLOGIA_DOC_MISSIONI = "cnrmissioni:tipologiaDocumentoMissione";


    public List<CMISFileAttachment> getAttachmentsDetail(Long idDettagliorimborso) throws ComponentException ;

    public List<StorageObject> getChildrenDettaglio(RimborsoMissioneDettagli dettaglio) ;

    public List<StorageObject> getChildrenDettaglio(RimborsoMissioneDettagli dettaglio, Boolean recuperoFileEliminati);

    public StorageObject getFolderDettaglioRimborso(RimborsoMissioneDettagli dettaglio) throws ComponentException ;

    public DatiFlusso recuperoDatiFlusso(RimborsoMissione rimborsoMissione, Integer anno, Account account);

    public CMISRimborsoMissione create(RimborsoMissione rimborsoMissione) throws ComponentException;



    @Transactional(readOnly = true)
    public StorageObject salvaStampaRimborsoMissioneSuCMIS(byte[] stampa, RimborsoMissione rimborsoMissione) throws ComponentException;



    public String createFolderRimborsoMissione(RimborsoMissione rimborsoMissione) ;

    public String createFolderRimborsoMissioneDettaglio(RimborsoMissioneDettagli dettaglio, String path) ;

    public Map<String, Object> getMetadataPropertiesFolderRimborso(RimborsoMissione rimborsoMissione);



    public StorageObject getObjectRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException;

    public String getNodeRefFolderDettaglioRimborso(RimborsoMissioneDettagli dettagliorimborso);

    public CMISFileAttachment uploadAttachmentDetail(RimborsoMissioneDettagli rimborsoMissioneDettagli, InputStream inputStream, String name, MimeTypes mimeTypes);

    public Map<String, Object> createMetadataForFileRimborsoMissione(String currentLogin, CMISRimborsoMissione cmisRimborsoMissione);


    @Transactional(readOnly = true)
    public void avviaFlusso(RimborsoMissione rimborsoMissione) throws ComponentException;

    public void controlloEsitenzaGiustificativoDettaglio(RimborsoMissione rimborsoMissione);

    public String getNodeRefRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException ;

    public StorageObject recuperoFolderRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException;

    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException ;




    public InputStream getStreamRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException;

    public StoragePath buildFolderRimborsoMissione(RimborsoMissione rimborsoMissione);
    public CMISFileAttachment uploadAttachmentRimborsoMissione(RimborsoMissione rimborsoMissione, Long idRimborsoMissione,
                                                               InputStream inputStream, String name, MimeTypes mimeTypes);

    public CMISFileAttachment uploadAttachmentAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione, Long idAnnullamentoRimborsoMissione,
                                                                           InputStream inputStream, String name, MimeTypes mimeTypes);
    public List<CMISFileAttachment> getAttachmentsRimborsoMissione(RimborsoMissione rimborsoMissione, Long idRimborsoMissione);

    public List<CMISFileAttachment> getAttachmentsAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione, Long idAnnullamentoRimborsoMissione);

    public List<StorageObject> getDocumentsAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione);
    public List<StorageObject> getDocumentsAllegatiRimborsoMissione(RimborsoMissione rimborsoMissione, Boolean recuperoFileEliminati);

    public List<StorageObject> getDocumentsAllegatiAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione) ;

    public Map<String, byte[]> getFileRimborsoMissione(RimborsoMissione rimborsoMissione) ;
    public StorageObject getStorageRimborsoMissione(RimborsoMissione rimborsoMissione) throws ComponentException;

    public List<StorageObject> getDocumentsRimborsoMissione(RimborsoMissione missione) throws ComponentException;

    public InputStream getResource(StorageObject so);
}
