package it.cnr.si.missioni.repository;

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.jada.criteria.Criteria;
import it.cnr.jada.criteria.Criterion;
import it.cnr.jada.criteria.Order;
import it.cnr.jada.criteria.Projection;

import java.io.Serializable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

public interface CRUDComponentSession<T extends OggettoBulk> extends it.cnr.jada.ejb.session.CRUDComponentSession<T>{
	public EntityManager getManager();

	public boolean hasForeignKey(T oggettobulk, String...attributes)
			throws ComponentException;

	public void lockBulk(T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException;
	public List eseguiQuery(Criteria criteria);
	public Criteria preparaCriteria(Class<T> bulkClass, Criterion criterionList, Projection projection, Order... order);
	public T findById(Class<T> bulkClass, Serializable id) throws ComponentException;
	public List<T> findByCriterion(Class<T> bulkClass, Criterion criterion, Order... order) throws ComponentException;
	public T creaConBulk(T model) throws ComponentException;
	public T modificaConBulk(T oggettobulk)  throws ComponentException;
	public void eliminaConBulk(T model)  throws ComponentException;
	public List<Object> findByProjection(Class<T> bulkClass, Projection projection, Criterion criterion, boolean useBeanResultTransformer, Order... order) throws ComponentException;
}
