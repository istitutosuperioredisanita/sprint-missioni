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

import it.cnr.jada.bulk.BulkHome;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.bulk.annotation.JadaOneToMany;
import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.CRUDComponentSession;
import it.cnr.jada.ejb.session.CRUDComponentSessionBean;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.OptimisticLockException;

import net.bzdyl.ejb3.criteria.projections.Projections;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractCRUDServiceBean<T extends OggettoBulk> extends
		CRUDComponentSessionBean<T> implements CRUDComponentSession<T> {
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public T persist(Principal principal, T model)
			throws ComponentException {
		return super.persist(principal, model);
	};

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public T creaConBulk(Principal principal, T model)
			throws ComponentException {
		return super.creaConBulk(principal, model);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public T modificaConBulk(Principal principal, T model)
			throws ComponentException {
		return super.modificaConBulk(principal, model);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void eliminaConBulk(Principal principal, T model)
			throws ComponentException {
		model = findByPrimaryKey(principal, model);
		model.setToBeDeleted();
		super.eliminaConBulk(principal, model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean hasForeignKey(Principal principal, T oggettobulk, String...attributes) throws ComponentException {
    	List<Field> attributi = Arrays.asList(oggettobulk.getClass().getDeclaredFields());
		for (Field attributo : attributi) {
			if (attributes != null && !Arrays.asList(attributes).isEmpty() &&
					!Arrays.asList(attributes).contains(attributo.getName()))
				continue;
			JadaOneToMany jadaOneToMany = attributo.getAnnotation(JadaOneToMany.class);
			if (jadaOneToMany != null){
				BulkHome home = getHomeClass(jadaOneToMany.targetEntity());
				CriterionList criterionList = new CriterionList(Restrictions.eq(jadaOneToMany.mappedBy(), oggettobulk));
				Long result = (Long)home.selectByProjection(principal, Projections.rowCount(), criterionList).prepareQuery(getManager()).getSingleResult();
				if (result.compareTo(Long.valueOf(0))==1)
					return true;
			}
		}
		return false;
	}
	
	public void lockBulk(Principal principal, T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException {
		super.lockBulk(principal, oggettobulk);
	}

}
