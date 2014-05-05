package net.yech.rabbitmq;

public class Config {

    private String host;

    private String virtualHost;

    private String user;

    private String password;


    public Config(String host, String virtualHost, String user, String password) {
        this.host = host;
        this.virtualHost = virtualHost;
        this.user = user;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
