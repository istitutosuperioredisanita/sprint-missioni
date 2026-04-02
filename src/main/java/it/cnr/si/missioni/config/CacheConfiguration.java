package it.cnr.si.missioni.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.impl.HazelcastInstanceFactory;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

@Configuration
@ConditionalOnProperty("cnr.cache.hazelcast.packages")
@EnableConfigurationProperties(CacheConfigurationProperties.class)
@EnableCaching
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    private static HazelcastInstance hazelcastInstance;

    private CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Closing Cache Manager");
        Hazelcast.shutdownAll();
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        log.debug("Starting HazelcastCacheManager");
        cacheManager = new com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance);
        return cacheManager;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(CacheConfigurationProperties cacheConfigurationProperties) {
        log.debug("Configuring Hazelcast");

        Config config = new Config();

        String mancenter = cacheConfigurationProperties.getMancenter();
        if (mancenter != null && !mancenter.isBlank()) {
            log.info("Using Management Center: {}", mancenter);
            ManagementCenterConfig mc = new ManagementCenterConfig();
            mc.setConsoleEnabled(true);
            config.setManagementCenterConfig(mc);
        } else {
            log.info("No Management Center configured");
        }

        String hazelcastInstanceName = cacheConfigurationProperties.getName();
        Integer hazelcastPort = cacheConfigurationProperties.getPort();
        Integer hazelcastMulticastPort = cacheConfigurationProperties.getMulticastPort();
        String members = cacheConfigurationProperties.getMembers();

        log.info("hazelcast instance name: {}", hazelcastInstanceName);

        config.setInstanceName(hazelcastInstanceName);
        config.getNetworkConfig().setPort(hazelcastPort);
        config.getNetworkConfig().setPortAutoIncrement(false);

        Optional.ofNullable(cacheConfigurationProperties.getPublicAddress())
                .ifPresent(address -> config.getNetworkConfig().setPublicAddress(address));

        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);

        if (members != null && !members.isBlank()) {
            log.info("TCP members: {}", members);
            config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
            Arrays.stream(members.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(member -> config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(member));
        } else if (hazelcastMulticastPort != null) {
            log.info("Multicast on port {}", hazelcastMulticastPort);
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
            config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(hazelcastMulticastPort);
            config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        } else {
            config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        }

        config.getMapConfigs().put("default", initializeDefaultMapConfig());

        Arrays.stream(cacheConfigurationProperties.getPackages().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(cachePackage -> {
                    config.getMapConfigs().put(
                            cachePackage,
                            initializeDomainMapConfig(cacheConfigurationProperties.getTtl())
                    );
                    log.info("package {} added to cache configuration", cachePackage);
                });

        // Configurazione esplicita per Spring Session
        config.getMapConfigs().put("spring:session:sessions", springSessionMapConfig());

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        return hazelcastInstance;
    }

    private MapConfig initializeDefaultMapConfig() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setBackupCount(1);

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE);
        evictionConfig.setSize(256);

        mapConfig.setEvictionConfig(evictionConfig);
        return mapConfig;
    }

    private MapConfig initializeDomainMapConfig(Integer ttl) {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setTimeToLiveSeconds(ttl != null ? ttl : 3600);
        return mapConfig;
    }

    private MapConfig springSessionMapConfig() {
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("spring:session:sessions");
        mapConfig.setBackupCount(1);
        mapConfig.setTimeToLiveSeconds(1800);

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(10000);

        mapConfig.setEvictionConfig(evictionConfig);

        mapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "principalName"));
        return mapConfig;
    }

    public static HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }
}