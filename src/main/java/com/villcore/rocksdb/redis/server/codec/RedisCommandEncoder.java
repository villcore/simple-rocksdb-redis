package com.villcore.rocksdb.redis.server.codec;

import com.villcore.rocksdb.redis.server.command.RedisResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisCommandEncoder extends MessageToByteEncoder<RedisResp> {

    private static final Logger log = LoggerFactory.getLogger(RedisCommandEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisResp redisResp, ByteBuf out) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (ByteBuf byteBuf : redisResp.getResp()) {
            out.writeBytes(byteBuf);
//            sb.append(byteBuf.toString(byteBuf.readerIndex(), byteBuf.writerIndex(), StandardCharsets.UTF_8));
        }
        log.info("Encode redis command, byte buf size {}, content {}", redisResp.getResp().length, sb.toString());
    }
}
