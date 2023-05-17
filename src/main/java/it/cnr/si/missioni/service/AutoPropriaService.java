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

package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;
import it.cnr.si.missioni.repository.AutoPropriaRepository;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.OptimisticLockException;
import java.util.List;

/**
 * Service class for managing users.
 */
@Service
public class AutoPropriaService {

    private final Logger log = LoggerFactory.getLogger(AutoPropriaService.class);

    @Autowired
    private AutoPropriaRepository autoPropriaRepository;

    @Autowired
    private CRUDComponentSession<AutoPropria> crudServiceBean;

    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public List<AutoPropria> getAutoProprie(String user) {
        return autoPropriaRepository.getAutoProprie(user);
    }

    @Transactional(readOnly = true)
    public AutoPropria getAutoPropria(String user, String targa) {
        return autoPropriaRepository.getAutoPropria(user, targa);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AutoPropria createAutoPropria(String user, AutoPropria autoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        autoPropria.setUid(user);
        autoPropria.setUser(securityService.getCurrentUserLogin());
        autoPropria.setToBeCreated();
        validaCRUD(autoPropria);
        autoPropria = crudServiceBean.creaConBulk(autoPropria);
        log.debug("Created Information for User: {}", autoPropria);
        return autoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AutoPropria updateAutoPropria(AutoPropria autoPropria) throws AwesomeException,
            ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {

        AutoPropria autoPropriaDB = crudServiceBean.findById(AutoPropria.class, autoPropria.getId());

        if (autoPropriaDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Auto Propria da aggiornare inesistente.");

        autoPropriaDB.setCartaCircolazione(autoPropria.getCartaCircolazione());
        autoPropriaDB.setMarca(autoPropria.getMarca());
        autoPropriaDB.setModello(autoPropria.getModello());
        autoPropriaDB.setTarga(autoPropria.getTarga());
        autoPropriaDB.setPolizzaAssicurativa(autoPropria.getPolizzaAssicurativa());
        autoPropriaDB.setToBeUpdated();

//		//effettuo controlli di validazione operazione CRUD
        validaCRUD(autoPropriaDB);

        autoPropria = crudServiceBean.modificaConBulk(autoPropriaDB);

        log.debug("Updated Information for Dati Patente: {}", autoPropria);
        return autoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAutoPropria(Long idAutoPropria) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
        AutoPropria autoPropria = crudServiceBean.findById(AutoPropria.class, idAutoPropria);

        //effettuo controlli di validazione operazione CRUD
        if (autoPropria != null) {
            autoPropria.setToBeDeleted();
            crudServiceBean.eliminaConBulk(autoPropria);
        }
    }

    private void validaCRUD(AutoPropria autoPropria) throws AwesomeException {
        if (autoPropria != null) {
            if (StringUtils.isEmpty(autoPropria.getMarca())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": marca");
            } else if (StringUtils.isEmpty(autoPropria.getModello())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": modello");
            } else if (StringUtils.isEmpty(autoPropria.getTarga())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": targa");
            } else if (StringUtils.isEmpty(autoPropria.getCartaCircolazione())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Carta Circolazione");
            } else if (StringUtils.isEmpty(autoPropria.getPolizzaAssicurativa())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Polizza Assicurativa");
            }
        }
    }
}
