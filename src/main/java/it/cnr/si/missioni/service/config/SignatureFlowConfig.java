package it.cnr.si.missioni.service.config;

import it.iss.si.signature.flow.config.SignatureFlowConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Integrazione di signature-flow per la firma dei documenti.
 * <p>
 * I servizi della libreria si attivano solo con {@code signature-flow.driver=HAPPYSIGN}.
 * Il profilo {@code iss} è necessario perché HappySign e ACE sono forniti dal profilo stesso.
 */
@Configuration
@Profile("iss")
@Import(SignatureFlowConfiguration.class)
public class SignatureFlowConfig {
}