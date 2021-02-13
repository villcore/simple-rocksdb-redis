package com.villcore.rocksdb.redis.server.codec;

import com.villcore.rocksdb.redis.server.command.RedisCommand;
import com.villcore.rocksdb.redis.server.command.RedisResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RedisCommandHandler extends SimpleChannelInboundHandler<ByteBuf[]> {

    private static final Logger log = LoggerFactory.getLogger(RedisCommandHandler.class);

    public RedisCommandHandler() {
        super(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf[] msg) throws Exception {
        ByteBuf commandCode = msg[0];
        RedisCommand redisCommand = RedisCommand.getCommand(commandCode);
        log.info("Handle redis command code {}, current command {} ", commandCode.toString(StandardCharsets.UTF_8), redisCommand);
        RedisResp redisResp = redisCommand.handleCommand(msg);
        ctx.writeAndFlush(redisResp);
    }
}
