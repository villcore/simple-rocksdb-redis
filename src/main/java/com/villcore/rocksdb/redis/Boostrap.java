package com.villcore.rocksdb.redis;

import com.villcore.rocksdb.redis.server.Server;
import com.villcore.rocksdb.redis.server.ServerConfig;
import com.villcore.rocksdb.redis.server.command.CommandInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class Boostrap {

    private static final Logger log = LoggerFactory.getLogger(Boostrap.class);

    public static void main(String[] args) {
        log.info("SimpleRocksdbRedis start...");

        CommandInitializer commandInitializer = new CommandInitializer(null);
        commandInitializer.initializeCommand();

        ServerConfig serverConfig = ServerConfig.Builder.create()
                .host("0.0.0.0")
                .port(6379)
                .build();
        Server server = new Server(serverConfig);
        server.startup();

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
    }
}
