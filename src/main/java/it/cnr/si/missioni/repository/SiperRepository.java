package it.cnr.si.missioni.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Francesco Uliana <francesco@uliana.it> on 04/02/16.
 */
@Repository
public class SiperRepository {

    private final static Logger log = LoggerFactory.getLogger(SiperRepository.class);
    public static final String EMAIL_COMUNICAZIONI = "email_comunicazioni";

    @Value("${siper.username:#{null}}")
    private String username;

    @Value("${siper.password:#{null}}")
    private String password;

    @Value("${siper.url:#{null}}")
    private String siperUrl;

    @Value("${siper.urldett:#{null}}")
    private String siperUrldett;


    @Value("${siper.urlboss:#{null}}")
    private String siperUrlBoss;

    private RestTemplate restTemplate;

    @PostConstruct
    public void setUp() {

        restTemplate = new RestTemplate();

        Charset charset = Charset.forName("UTF-8");
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(charset);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        log.info("siper connection to url {} with user {}", siperUrl, username);

        interceptors.add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                byte[] token = Base64.encode((username + ":" + password).getBytes());
                request.getHeaders().add("Authorization", "Basic " + new String(token));
                return execution.execute(request, body);
            }
        });

        restTemplate.getMessageConverters().add(0, stringHttpMessageConverter);
        restTemplate.setInterceptors(interceptors);
    }

    /**
     *
     * Returns the user account metadata
     *
     * @param id user id
     * @return account metadata
     */
    public Optional<Map> getAccountProperties(String id) {


        try {
            String json = restTemplate.getForObject(siperUrl + id, String.class);

            Map m = new ObjectMapper().readValue(json, Map.class);
            log.info(m.toString());
            return Optional.of(m);

        } catch(HttpClientErrorException e) {
            log.warn("{} not found", id, e);
            return Optional.empty();
        } catch (IOException e) {
            log.error("siper error, id {}", id, e);
            return Optional.empty();
        }

    }


    /**
     *
     * Return the user id (normally <i>name.surname</i>) for a given <i>matricola</i>
     *
     * @param matricola
     * @return user id
     */
    public Optional<String> getId(String matricola) {

        Optional<String> userId;

        String json;

        try {
            json = restTemplate.getForObject(siperUrldett + matricola, String.class);
        } catch(HttpClientErrorException e) {
            log.warn("unable to find matricola {}", matricola, e);
            return Optional.empty();
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map m = objectMapper.readValue(json, Map.class);
            String login = m.get("uid").toString();
            userId = Optional.of(login);
        } catch (IOException e) {
            userId = Optional.empty();
        }


        return userId;


    }
    /**
     *
     * get the email address of the boss of the office having id :sedeId
     *
     * @param sedeId office id
     * @param ruolo ruolo
     * @return boss email address
     */
    public Optional<String> getBossEmail(String sedeId, String ruolo) {
        Optional<String> value;
        try {
            String json = restTemplate
                    .getForObject(siperUrlBoss + "?" +
                                    "sedeId={sedeId}&userinfo={userinfo}&ruolo={ruolo}",
                            String.class, sedeId, Boolean.TRUE.toString(), ruolo);

            List l = new ObjectMapper().readValue(json, List.class);
            assert 1 == l.size();
            log.debug(l.toString());
            Object boss = l.get(0);
            assert boss instanceof Map;
            Map bossDetails = (Map) boss;
            assert bossDetails.containsKey(EMAIL_COMUNICAZIONI);
            String mail = bossDetails.get(EMAIL_COMUNICAZIONI).toString();
            value = Optional.of(mail);
        } catch (RestClientException | IOException e) {
            log.warn("unable to get {} boss", sedeId, e);
            value = Optional.empty();
        }
        return value;
    }
    /**
     *
     * get the email address of the boss of the office having id :sedeId
     *
     * @param sedeId office id
     * @return boss email address
     */
    public Optional<String> getBossEmail(String sedeId) {
        return getBossEmail(sedeId, "dircds");
    }


}
