package com.villcore.rocksdb.redis.server;

public class ServerConfig {

    private final String host;
    private final int port;

    private ServerConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static final class Builder {

        private String host;
        private int port;

        public static Builder create() {
            return new Builder();
        }

        private Builder() {}

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }
}
