package it.cnr.si.missioni.amq.domain;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.cnr.si.missioni.util.DateUtils;

/**
 * Created by francesco on 09/03/17.
 */
public class Missione {

    private TypeMissione tipo_missione;
    private String codice_sede;
    private Long id;
    private String matricola;
    private ZonedDateTime data_inizio;
    private ZonedDateTime data_fine;
    private Long id_ordine;

    public Missione(TypeMissione tipoMissione, Long id, String codiceSede, String matricola, ZonedDateTime data_inizio, ZonedDateTime data_fine, Long idOrdine) {
        this.codice_sede = codiceSede;
        this.tipo_missione = tipoMissione;
        this.matricola = matricola;
        this.data_inizio = data_inizio;
        this.data_fine = data_fine;
        this.id = id;
        this.id_ordine = idOrdine;
    }

    public String getMatricola() {
        return matricola;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.PATTERN_DATETIME_WITH_TIMEZONE)
    public ZonedDateTime getData_inizio() {
        return data_inizio;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.PATTERN_DATETIME_WITH_TIMEZONE)
    public ZonedDateTime getData_fine() {
        return data_fine;
    }

    public Long getId() {
        return id;
    }



	public TypeMissione getTipo_missione() {
		return tipo_missione;
	}

	public String getCodice_sede() {
		return codice_sede;
	}

	public Long getId_ordine() {
		return id_ordine;
	}


    @Override
    public String toString() {
        return "Missione{" +
                "tipoMissione='" + tipo_missione + '\'' +
                "codiceSede='" + codice_sede + '\'' +
                "matricola='" + matricola + '\'' +
                ", data_inizio=" + data_inizio +
                ", data_fine=" + data_fine +
                ", id=" + id +
                ", idOrdine=" + id_ordine +
                '}';
    }
}