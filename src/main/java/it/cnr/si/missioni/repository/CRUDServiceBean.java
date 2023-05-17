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
package it.cnr.si.missioni.repository;


import it.cnr.jada.GenericPrincipal;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.criteria.Criteria;
import it.cnr.jada.criteria.Criterion;
import it.cnr.jada.criteria.Order;
import it.cnr.jada.criteria.Projection;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class CRUDServiceBean<T extends OggettoBulk> extends AbstractCRUDServiceBean<T> implements CRUDComponentSession<T> {
    @Autowired
    SecurityService securityService;
    @PersistenceContext
    private EntityManager em;

    public EntityManager getManager() {
        return em;
    }

    public List eseguiQuery(Criteria criteria) {
        return criteria.prepareQuery(getManager()).getResultList();
    }

    public Criteria preparaCriteria(Class<T> bulkClass,
                                    Criterion criterionList, Projection projection, Order... order) {
        return select(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), bulkClass, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
    }
}
