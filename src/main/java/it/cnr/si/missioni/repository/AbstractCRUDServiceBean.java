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
import it.cnr.jada.bulk.BulkHome;
import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.jada.bulk.annotation.JadaOneToMany;
import it.cnr.jada.criteria.Criterion;
import it.cnr.jada.criteria.Order;
import it.cnr.jada.criteria.Projection;
import it.cnr.jada.criteria.projections.Projections;
import it.cnr.jada.criteria.restrictions.Restrictions;
import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.ejb.session.CRUDComponentSession;
import it.cnr.jada.ejb.session.*;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCRUDServiceBean<T extends OggettoBulk> extends
        CRUDComponentSessionBean<T> implements CRUDComponentSession<T> {
    @Autowired
    SecurityService securityService;

    @Transactional(propagation = Propagation.REQUIRED)
    public T persist(T model)
            throws ComponentException {
        return super.persist(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), model);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T creaConBulk(T model)
            throws ComponentException {
        return super.creaConBulk(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), model);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T modificaConBulk(T model)
            throws ComponentException {
        return super.modificaConBulk(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), model);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void eliminaConBulk(T model)
            throws ComponentException {
        Principal principal = new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI));
        model = super.findByPrimaryKey(principal, model);
        model.setToBeDeleted();
        super.eliminaConBulk(principal, model);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean hasForeignKey(T oggettobulk, String... attributes) throws ComponentException {
        Principal principal = new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI));
        Field[] attributi = oggettobulk.getClass().getDeclaredFields();
        for (Field attributo : attributi) {
            if (attributes != null && !Arrays.asList(attributes).isEmpty() &&
                    !Arrays.asList(attributes).contains(attributo.getName()))
                continue;
            JadaOneToMany jadaOneToMany = attributo.getAnnotation(JadaOneToMany.class);
            if (jadaOneToMany != null) {
                BulkHome home = getHomeClass(jadaOneToMany.targetEntity());
                CriterionList criterionList = new CriterionList(Restrictions.eq(jadaOneToMany.mappedBy(), oggettobulk));
                Long result = (Long) home.selectByProjection(principal, Projections.rowCount(), criterionList).prepareQuery(getManager()).getSingleResult();
                if (result.compareTo(Long.valueOf(0)) == 1)
                    return true;
            }
        }
        return false;
    }

    public void lockBulk(T oggettobulk) throws PersistencyException, ComponentException, BusyResourceException, OptimisticLockException {
        super.lockBulk(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), oggettobulk);
    }

    public T findById(Class<T> bulkClass, Serializable id) throws ComponentException {
        return super.findById(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), bulkClass, id);
    }

    public List<T> findByCriterion(Class<T> bulkClass, Criterion criterion, Order... order) throws ComponentException {
        return super.findByCriterion(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), bulkClass, criterion, order);
    }

    public List<Object> findByProjection(Class<T> bulkClass,
                                         Projection projection, Criterion criterion, boolean useBeanResultTransformer, Order... order) throws ComponentException {
        return super.findByProjection(new GenericPrincipal(Optional.ofNullable(securityService.getCurrentUserLogin()).orElse(Costanti.USER_CRON_MISSIONI)), bulkClass, projection, criterion, useBeanResultTransformer, order);
    }
}
