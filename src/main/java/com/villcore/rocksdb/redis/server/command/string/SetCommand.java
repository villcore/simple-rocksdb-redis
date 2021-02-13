package com.villcore.rocksdb.redis.server.command.string;

import com.villcore.rocksdb.redis.server.command.RedisCommand;
import com.villcore.rocksdb.redis.server.command.RedisResp;
import com.villcore.rocksdb.redis.store.DataStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

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
    public RedisResp handleCommand(ByteBuf[] param) {
        log.info("Handle '{}' command ", getClass().getSimpleName());
        ByteBuf byteBuf = Unpooled.copiedBuffer("+OK\r\n".getBytes(StandardCharsets.UTF_8));
        return new RedisResp(new ByteBuf[]{byteBuf});
    }
}
