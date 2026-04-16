package it.cnr.si.missioni;

import it.cnr.si.missioni.config.Constants;
import it.cnr.si.missioni.config.DefaultProfileUtil;
import it.cnr.si.missioni.config.JHipsterProperties;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication(
        scanBasePackages = {
                "it.cnr.si.missioni",
                "it.cnr.si.spring.storage"
        }
)
@EnableConfigurationProperties({JHipsterProperties.class, LiquibaseProperties.class})
public class MissioniApplication {

    private static final Logger log = LoggerFactory.getLogger(MissioniApplication.class);

    @Inject
    private Environment env;

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(MissioniApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();

        String applicationName = env.getProperty("spring.application.name", "application");
        String serverPort = env.getProperty("server.port", "8080");

        String localUrl = "http://127.0.0.1:" + serverPort;
        String detectedExternalUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;

        String externalUrl = env.getProperty("app.public-url");
        if (externalUrl == null || externalUrl.isBlank()) {
            externalUrl = detectedExternalUrl;
        }

        log.info(
                "\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}\n\t" +
                        "External: \t{}\n" +
                        "----------------------------------------------------------",
                applicationName,
                localUrl,
                externalUrl
        );
    }

    @PostConstruct
    public void initApplication() {
        log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run with both the 'dev' and 'prod' profiles at the same time.");
        }

        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }
}