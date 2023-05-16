/*******************************************************************************
 * Copyright 2008 Italian National Research Council
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 * @author Marco Spasiano
 ******************************************************************************/
package it.cnr.si.missioni.repository;


import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.cnr.jada.GenericPrincipal;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.criteria.Criteria;
import it.cnr.jada.criteria.Criterion;
import it.cnr.jada.criteria.Order;
import it.cnr.jada.criteria.Projection;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class CRUDServiceBean<T extends OggettoBulk> extends AbstractCRUDServiceBean<T> implements CRUDComponentSession<T> {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	SecurityService securityService;

	public EntityManager getManager() {
		return em;
	}
	public List eseguiQuery(Criteria criteria){
		return criteria.prepareQuery(getManager()).getResultList();
	}
	public Criteria preparaCriteria(Class<T> bulkClass,
			Criterion criterionList, Projection projection, Order... order){
		return select(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), bulkClass, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
	}
}
