package it.cnr.si.missioni.service;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.cnr.si.missioni.util.data.UtentePresidenteSpeciale;

@Service
public class UtentiPresidenteSpecialiService {
	private final Logger log = LoggerFactory.getLogger(UoService.class);

	@Autowired
	ConfigService configService;

	public UtentePresidenteSpeciale esisteUtente(String uid) {
		if (configService.getUtentiPresidenteSpeciali() != null
				&& configService.getUtentiPresidenteSpeciali().getUtentePresidenteSpeciale() != null) {
			for (Iterator<UtentePresidenteSpeciale> iteratorUsers = configService.getUtentiPresidenteSpeciali()
					.getUtentePresidenteSpeciale().iterator(); iteratorUsers.hasNext();) {
				UtentePresidenteSpeciale user = iteratorUsers.next();
				log.debug("Ricerca amministrativi per mail. Utente: " + user.getCodiceUtente());
				if (user != null && user.getCodiceUtente().equals(uid)) {
					return user;
				}
			}
		}
		return null;
	}

}
