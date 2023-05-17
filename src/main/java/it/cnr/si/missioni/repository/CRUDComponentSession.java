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

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.criteria.Criteria;
import it.cnr.jada.criteria.Criterion;
import it.cnr.jada.criteria.Order;
import it.cnr.jada.criteria.Projection;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.util.List;

public interface CRUDComponentSession<T extends OggettoBulk> extends it.cnr.jada.ejb.session.CRUDComponentSession<T> {
    EntityManager getManager();

    boolean hasForeignKey(T oggettobulk, String... attributes)
            throws ComponentException;

    void lockBulk(T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException;

    List eseguiQuery(Criteria criteria);

    Criteria preparaCriteria(Class<T> bulkClass, Criterion criterionList, Projection projection, Order... order);

    T findById(Class<T> bulkClass, Serializable id) throws ComponentException;

    List<T> findByCriterion(Class<T> bulkClass, Criterion criterion, Order... order) throws ComponentException;

    T creaConBulk(T model) throws ComponentException;

    T modificaConBulk(T oggettobulk) throws ComponentException;

    void eliminaConBulk(T model) throws ComponentException;

    List<Object> findByProjection(Class<T> bulkClass, Projection projection, Criterion criterion, boolean useBeanResultTransformer, Order... order) throws ComponentException;
}
