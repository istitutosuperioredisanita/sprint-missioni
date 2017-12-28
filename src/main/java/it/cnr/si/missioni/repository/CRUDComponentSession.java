package it.cnr.si.missioni.repository;

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import net.bzdyl.ejb3.criteria.Criteria;
import net.bzdyl.ejb3.criteria.Criterion;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.Projection;

import java.security.Principal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

public interface CRUDComponentSession<T extends OggettoBulk> extends it.cnr.jada.ejb.session.CRUDComponentSession<T>{
	public EntityManager getManager();

	public boolean hasForeignKey(Principal principal, T oggettobulk, String...attributes) 
			throws ComponentException;

	public void lockBulk(Principal principal, T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException;
	public List eseguiQuery(Criteria criteria);
	public Criteria preparaCriteria(Principal principal, Class<T> bulkClass, Criterion criterionList, Projection projection, Order... order);
}
