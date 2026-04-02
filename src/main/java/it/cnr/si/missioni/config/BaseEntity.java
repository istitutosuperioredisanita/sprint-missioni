package it.cnr.si.missioni.config;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Persistable<Serializable> {

    // ── Costanti CRUD status ──────────────────────────────────────────────
    public static final int UNDEFINED     = 0;
    public static final int TO_BE_CREATED = 1;
    public static final int TO_BE_UPDATED = 2;
    public static final int TO_BE_DELETED = 3;
    public static final int TO_BE_CHECKED = 4;
    public static final int NORMAL        = 5;

    // ── Campi di audit ────────────────────────────────────────────────────
    @CreatedBy
    @Column(name = "UTCR", nullable = false, updatable = false)
    private String utcr;

    @LastModifiedBy
    @Column(name = "UTUV", nullable = false)
    private String utuv;

    @CreatedDate
    @Column(name = "DACR", nullable = false, updatable = false)
    private Instant dacr;

    @LastModifiedDate
    @Column(name = "DUVA", nullable = false)
    private Instant duva;

    @Version
    @Column(name = "PG_VER_REC", nullable = false)
    private Long pgVerRec;

    // ── Stato CRUD (non persistito) ───────────────────────────────────────
    @Transient
    private int crudStatus = UNDEFINED;

    @Transient
    private String user; // non persiste nel DB, serve solo lato logica

    // ── Lifecycle JPA ─────────────────────────────────────────────────────
    @PostLoad
    void onLoad() {
        this.crudStatus = NORMAL;
    }

    @PostPersist
    void onPersisted() {
        this.crudStatus = NORMAL;
    }

    @PostUpdate
    void onUpdated() {
        this.crudStatus = NORMAL;
    }

    @PostRemove
    void onRemoved() {
        this.crudStatus = UNDEFINED;
    }

    // ── Persistable implementation ────────────────────────────────────────
    @Override
    public boolean isNew() {
        return crudStatus == TO_BE_CREATED || getId() == null;
    }

    // ── CRUD helpers ──────────────────────────────────────────────────────
    public int getCrudStatus() {
        return crudStatus;
    }

    public void setCrudStatus(int status) {
        this.crudStatus = status;
    }

    public boolean isNotNew() {
        return !isNew();
    }

    public boolean isToBeCreated() {
        return crudStatus == TO_BE_CREATED;
    }

    public boolean isToBeUpdated() {
        return crudStatus == TO_BE_UPDATED;
    }

    public boolean isToBeDeleted() {
        return crudStatus == TO_BE_DELETED;
    }

    public boolean isToBeChecked() {
        return crudStatus == TO_BE_CHECKED;
    }

    public void setToBeCreated() {
        if (crudStatus == TO_BE_DELETED) {
            setCrudStatus(NORMAL);
        } else if (crudStatus == UNDEFINED) {
            setCrudStatus(TO_BE_CREATED);
        }
    }

    public void setToBeUpdated() {
        if (crudStatus == NORMAL) {
            setCrudStatus(TO_BE_UPDATED);
        } else if (crudStatus == UNDEFINED) {
            setCrudStatus(TO_BE_CREATED);
        }
    }

    public void setToBeDeleted() {
        if (crudStatus == TO_BE_CREATED) {
            setCrudStatus(UNDEFINED);
        } else if (crudStatus != UNDEFINED) {
            setCrudStatus(TO_BE_DELETED);
        }
    }

    public void setToBeChecked() {
        if (crudStatus == NORMAL) {
            setCrudStatus(TO_BE_CHECKED);
        }
    }

    // ── Getter/setter audit ───────────────────────────────────────────────
    public String getUtcr() {
        return utcr;
    }

    public void setUtcr(String utcr) {
        this.utcr = utcr;
    }

    public String getUtuv() {
        return utuv;
    }

    public void setUtuv(String utuv) {
        this.utuv = utuv;
    }

    public Instant getDacr() {
        return dacr;
    }

    public void setDacr(Instant dacr) {
        this.dacr = dacr;
    }

    public Instant getDuva() {
        return duva;
    }

    public void setDuva(Instant duva) {
        this.duva = duva;
    }

    public Long getPgVerRec() {
        return pgVerRec;
    }

    public void setPgVerRec(Long pgVerRec) {
        this.pgVerRec = pgVerRec;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    // ── ID ────────────────────────────────────────────────────────────────
    @Override
    public abstract Serializable getId();
}