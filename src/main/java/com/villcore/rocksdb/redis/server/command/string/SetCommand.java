package com.villcore.rocksdb.redis.server.command.string;

import com.villcore.rocksdb.redis.server.command.RedisCommand;
import com.villcore.rocksdb.redis.server.command.RedisResp;
import com.villcore.rocksdb.redis.store.DataStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SetCommand extends RedisCommand {

    private static final Logger log = LoggerFactory.getLogger(SetCommand.class);

    public SetCommand(DataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ByteBuf getCommandCode() {
        return Unpooled.copiedBuffer("SET".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public RedisMessage handleCommand(List<RedisMessage> subList) {
        if (subList == null || subList.size() != 2) {
            return new ErrorRedisMessage("SET Command expected argument size is 2");
        }

        log.info("Handle '{}' command ", getClass().getSimpleName());
        return new SimpleStringRedisMessage("OK");
    }
}
