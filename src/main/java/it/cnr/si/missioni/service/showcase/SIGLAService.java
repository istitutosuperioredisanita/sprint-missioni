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

package it.cnr.si.missioni.service.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class SIGLAService {
    public NazioneJson getNazioni() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Nazione.json");
        try {
            return new ObjectMapper().readValue(is, NazioneJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle nazioni per lo showcase." + Utility.getMessageException(e));
        }
    }

    public TerzoPerCompensoJson getTerziPerCompenso() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/TerzoPerCompenso.json");
        try {
            return new ObjectMapper().readValue(is, TerzoPerCompensoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle TerzoPerCompenso per lo showcase." + Utility.getMessageException(e));
        }
    }

    public CdsJson getCds() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Cds.json");
        try {
            return new ObjectMapper().readValue(is, CdsJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Cds per lo showcase." + Utility.getMessageException(e));
        }
    }

    public UnitaOrganizzativaJson getUo() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Uo.json");
        try {
            return new ObjectMapper().readValue(is, UnitaOrganizzativaJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Uo per lo showcase." + Utility.getMessageException(e));
        }
    }

    public CdrJson getCdr() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Cdr.json");
        try {
            return new ObjectMapper().readValue(is, CdrJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Cdr per lo showcase." + Utility.getMessageException(e));
        }
    }

    public VoceJson getVoci() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Voce.json");
        try {
            return new ObjectMapper().readValue(is, VoceJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Voce per lo showcase." + Utility.getMessageException(e));
        }
    }

    public ProgettoJson getProgetti() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Progetto.json");
        try {
            return new ObjectMapper().readValue(is, ProgettoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Progetto per lo showcase." + Utility.getMessageException(e));
        }
    }

    public GaeJson getGae() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Gae.json");
        try {
            return new ObjectMapper().readValue(is, GaeJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Gae per lo showcase." + Utility.getMessageException(e));
        }
    }

    public ImpegnoJson getImpegno() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Impegno.json");
        try {
            return new ObjectMapper().readValue(is, ImpegnoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Impegno per lo showcase." + Utility.getMessageException(e));
        }
    }

    public ImpegnoGaeJson getImpegnoGae() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/ImpegnoGae.json");
        try {
            return new ObjectMapper().readValue(is, ImpegnoGaeJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Impegno Gae per lo showcase." + Utility.getMessageException(e));
        }
    }

    public TerzoJson getTerzo() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Terzo.json");
        try {
            return new ObjectMapper().readValue(is, TerzoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Terzo per lo showcase." + Utility.getMessageException(e));
        }
    }

    public BancaJson getBanca() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Banca.json");
        try {
            return new ObjectMapper().readValue(is, BancaJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Banca per lo showcase." + Utility.getMessageException(e));
        }
    }

    public InquadramentoJson getInquadramento() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Inquadramento.json");
        try {
            return new ObjectMapper().readValue(is, InquadramentoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Inquadramento per lo showcase." + Utility.getMessageException(e));
        }
    }

    public ModalitaPagamentoJson getModpag() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Modpag.json");
        try {
            return new ObjectMapper().readValue(is, ModalitaPagamentoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Modpag per lo showcase." + Utility.getMessageException(e));
        }
    }

    public TipoSpesaJson getTipoSpesa() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/TipoSpesa.json");
        try {
            return new ObjectMapper().readValue(is, TipoSpesaJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle TipoSpesa per lo showcase." + Utility.getMessageException(e));
        }
    }

    public DivisaJson getDivisa() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/Divisa.json");
        try {
            return new ObjectMapper().readValue(is, DivisaJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle Divisa per lo showcase." + Utility.getMessageException(e));
        }
    }

    public StatoPagamentoJson getStatoPagamento() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/StatoPagamento.json");
        try {
            return new ObjectMapper().readValue(is, StatoPagamentoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle StatoPagamento per lo showcase." + Utility.getMessageException(e));
        }
    }

    public TipoPastoJson getTipoPasto() {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/TipoPasto.json");
        try {
            return new ObjectMapper().readValue(is, TipoPastoJson.class);
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle TipoPasto per lo showcase." + Utility.getMessageException(e));
        }
    }
}
