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

import it.cnr.jada.GenericPrincipal;
import it.cnr.jada.bulk.BulkHome;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.bulk.annotation.JadaOneToMany;
import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.CRUDComponentSession;
import it.cnr.jada.ejb.session.CRUDComponentSessionBean;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;

import java.io.Serializable;
import java.lang.reflect.Field;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.OptimisticLockException;

import it.cnr.si.service.SecurityService;
import net.bzdyl.ejb3.criteria.Criterion;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.Projection;
import net.bzdyl.ejb3.criteria.projections.Projections;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractCRUDServiceBean<T extends OggettoBulk> extends
		CRUDComponentSessionBean<T> implements CRUDComponentSession<T> {
	@Autowired
	SecurityService securityService;

	@Transactional(propagation = Propagation.REQUIRED)
	public T persist(T model)
			throws ComponentException {
		return super.persist(new GenericPrincipal(securityService.getCurrentUserLogin()), model);
	};

	@Transactional(propagation = Propagation.REQUIRED)
	public T creaConBulk(T model)
			throws ComponentException {
		return super.creaConBulk(new GenericPrincipal(securityService.getCurrentUserLogin()), model);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public T modificaConBulk(T model)
			throws ComponentException {
		return super.modificaConBulk(new GenericPrincipal(securityService.getCurrentUserLogin()),  model);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void eliminaConBulk(T model)
			throws ComponentException {
		Principal principal = new GenericPrincipal(securityService.getCurrentUserLogin());
		model = super.findByPrimaryKey(principal, model);
		model.setToBeDeleted();
		super.eliminaConBulk(principal, model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean hasForeignKey(T oggettobulk, String...attributes) throws ComponentException {
		 Principal principal = new GenericPrincipal(securityService.getCurrentUserLogin());
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
	
	public void lockBulk(T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException {
		super.lockBulk(new GenericPrincipal(securityService.getCurrentUserLogin()), oggettobulk);
	}
	public T findById(Class<T> bulkClass, Serializable id) throws ComponentException{
		return super.findById(new GenericPrincipal(securityService.getCurrentUserLogin()), bulkClass, id);
	}
	public List<T> findByCriterion(Class<T> bulkClass, Criterion criterion, Order... order) throws ComponentException {
		return super.findByCriterion(new GenericPrincipal(securityService.getCurrentUserLogin()), bulkClass, criterion,order);
	}
	public List<Object> findByProjection(Class<T> bulkClass,
										 Projection projection, Criterion criterion, boolean useBeanResultTransformer, Order... order) throws ComponentException {
		return super.findByProjection(new GenericPrincipal(securityService.getCurrentUserLogin()), bulkClass, projection, criterion, useBeanResultTransformer,order);
	}
}
