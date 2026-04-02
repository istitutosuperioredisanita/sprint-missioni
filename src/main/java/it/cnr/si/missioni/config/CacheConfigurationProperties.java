package it.cnr.si.missioni.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by francesco on 26/06/17.
 */
@ConfigurationProperties("cnr.cache.hazelcast")
public class CacheConfigurationProperties {

    private String packages = "it.cnr.si";
    private String mancenter = null;
    private String name = "sprint";
    private Integer ttl = 3_600;
    private Integer port = 5701;
    private Integer multicastPort = 54327;
    private String members = "127.0.0.1";
    private String publicAddress;

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getMancenter() {
        return mancenter;
    }

    public void setMancenter(String mancenter) {
        this.mancenter = mancenter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(Integer multicastPort) {
        this.multicastPort = multicastPort;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }
}
