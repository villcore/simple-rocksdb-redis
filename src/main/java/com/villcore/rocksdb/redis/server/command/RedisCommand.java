package com.villcore.rocksdb.redis.server.command;

import com.villcore.rocksdb.redis.store.DataStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class RedisCommand {

    private final static Map<ByteBuf, RedisCommand> registeredCommand = new HashMap<>();

    public static  <T extends RedisCommand>  void registerCommand(T redisCommand) {
        Objects.requireNonNull(redisCommand);
        registeredCommand.put(
                Unpooled.copiedBuffer(redisCommand.getCommandCode()),
                redisCommand
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends RedisCommand> T getCommand(ByteBuf commandCodeBuf) {
        return (T) registeredCommand.get(commandCodeBuf);
    }

    private DataStore dataStore;

    public RedisCommand(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public abstract ByteBuf getCommandCode();

    public abstract RedisResp handleCommand(ByteBuf[] param);
}
