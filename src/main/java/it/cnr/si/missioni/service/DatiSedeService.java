package it.cnr.si.missioni.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.repository.DatiSedeRepository;

/**
 * Service class for managing users.
 */
@Service
public class DatiSedeService {

    private final Logger log = LoggerFactory.getLogger(DatiSedeService.class);

    @Autowired
    private DatiSedeRepository datiSedeRepository;

    @Transactional(readOnly = true)
    public DatiSede getDatiSede(String sede, LocalDate data) {
        return datiSedeRepository.getDatiSede(sede, data);
    }

    @Transactional(readOnly = true)
    public DatiSede getDatiSede(String sede, ZonedDateTime data) {
        return getDatiSede(sede, data.toLocalDate());
    }
}
