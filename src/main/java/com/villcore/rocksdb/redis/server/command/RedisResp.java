package com.villcore.rocksdb.redis.server.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import java.nio.charset.StandardCharsets;

public class RedisResp {

    private static final byte ERROR_PREFIX = '-';
    private static final byte CARRIAGE_RETURN = (byte) '\r';
    private static final byte LINE_FEED = (byte) '\n';

    public static final ByteBuf INVALID_COMMAND_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.copiedBuffer("INVALID COMMAND", StandardCharsets.UTF_8))).markReaderIndex();

    public static final ByteBuf UNKNOWN_COMMAND_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.copiedBuffer("UNKNOWN COMMAND", StandardCharsets.UTF_8))).markReaderIndex();

    public static final ByteBuf CR_LF_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.copiedBuffer("\r'\n", StandardCharsets.UTF_8))).markReaderIndex();

    public static final ByteBuf ERROR_PREFIX_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.copiedBuffer(new byte[]{ERROR_PREFIX}))).markReaderIndex();

    public static final ByteBuf CARRIAGE_RETURN_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.wrappedBuffer(new byte[]{CARRIAGE_RETURN}))).markReaderIndex();

    public static final ByteBuf LINE_FEED_BYTE_BUF = Unpooled.wrappedUnmodifiableBuffer(
            Unpooled.unreleasableBuffer(
                    Unpooled.wrappedBuffer(new byte[]{LINE_FEED}))).markReaderIndex();

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
    private static final RedisResp INVALID_COMMAND = new RedisResp(
            errorResp(INVALID_COMMAND_BYTE_BUF.duplicate().resetReaderIndex()));

    /**
     * unknown command redis resp
     */
    private static final RedisResp UNKNOWN_COMMAND = new RedisResp(
           errorResp(UNKNOWN_COMMAND_BYTE_BUF.duplicate().resetReaderIndex()));

    public static RedisMessage invalidCommandRedisResp() {
        return new SimpleStringRedisMessage("INVALID COMMAND");
    }

    public static RedisMessage unknownCommandRedisResp() {
        return new SimpleStringRedisMessage("UNKNOWN COMMAND");
    }

    public static RedisMessage errorCommandRedisResp() {
        return new ErrorRedisMessage("UNKNOWN ERR");
    }

    public static ByteBuf[] errorResp(ByteBuf byteBuf) {
        return new ByteBuf[]{
                ERROR_PREFIX_BYTE_BUF.duplicate().resetReaderIndex(),
                byteBuf,
                CARRIAGE_RETURN_BYTE_BUF.duplicate().resetReaderIndex(),
                LINE_FEED_BYTE_BUF.duplicate().resetReaderIndex()};
    }
}
