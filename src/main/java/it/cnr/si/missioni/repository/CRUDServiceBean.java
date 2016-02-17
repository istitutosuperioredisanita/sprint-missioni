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

import it.cnr.jada.bulk.OggettoBulk;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class CRUDServiceBean<T extends OggettoBulk> extends AbstractCRUDServiceBean<T> implements CRUDComponentSession<T> {
	@PersistenceContext
	private EntityManager em;

	public EntityManager getManager() {
		return em;
	}
}
