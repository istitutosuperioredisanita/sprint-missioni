package it.cnr.si.missioni.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.si.missioni.domain.custom.persistence.Parametri;
import it.cnr.si.missioni.repository.ParametriRepository;

/**
 * Service class for managing users.
 */
@Service
public class ParametriService {

    private final Logger log = LoggerFactory.getLogger(ParametriService.class);

    @Autowired
    private ParametriRepository parametriRepository;

    @Transactional(readOnly = true)
    public Parametri getParametri() {
        return parametriRepository.getParametri();
    }
}
