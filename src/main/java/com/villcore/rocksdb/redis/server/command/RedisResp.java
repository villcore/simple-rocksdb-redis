package com.villcore.rocksdb.redis.server.command;

import com.villcore.rocksdb.redis.server.codec.RedisCommandDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class RedisResp {

    private ByteBuf[] resp;

    public RedisResp(ByteBuf[] resp) {
        this.resp = resp;
    }

    public ByteBuf[] getResp() {
        return resp;
    }

    public void setResp(ByteBuf[] resp) {
        this.resp = resp;
    }

    /**
     * invalid command redis resp
     */
    private static final RedisResp INVALID_COMMAND_REDIS_RESP = new RedisResp(
            RedisCommandDecoder.errorResp(Unpooled.copiedBuffer("INVALID COMMAND", StandardCharsets.UTF_8)));

    /**
     * unknown command redis resp
     */
    private static final RedisResp UNKNOWN_COMMAND_REDIS_RESP = new RedisResp(
            RedisCommandDecoder.errorResp(Unpooled.copiedBuffer("UNKNOWN COMMAND", StandardCharsets.UTF_8)));

    public static RedisResp invalidCommandRedisResp() {
        return INVALID_COMMAND_REDIS_RESP;
    }

    public static RedisResp unknownCommandRedisResp() {
        return UNKNOWN_COMMAND_REDIS_RESP;
    }
}
