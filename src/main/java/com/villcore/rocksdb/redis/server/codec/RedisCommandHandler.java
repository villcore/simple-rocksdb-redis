package com.villcore.rocksdb.redis.server.codec;

import com.villcore.rocksdb.redis.server.command.RedisCommand;
import com.villcore.rocksdb.redis.server.command.RedisResp;
import com.villcore.rocksdb.redis.server.monitor.ServerMonitor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RedisCommandHandler extends SimpleChannelInboundHandler<RedisMessage> {

    private static final Logger log = LoggerFactory.getLogger(RedisCommandHandler.class);

    public RedisCommandHandler() {
        super(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisMessage msg) {
        handleRedisMessage(ctx, msg);
    }

    private void handleRedisMessage(ChannelHandlerContext ctx, RedisMessage msg) {
        if (msg instanceof ArrayRedisMessage) {
            ArrayRedisMessage arrayRedisMessage = (ArrayRedisMessage) msg;
            handleArrayRedisMessage(arrayRedisMessage, ctx);
        } else {
            ctx.writeAndFlush(RedisResp.errorCommandRedisResp());
        }
    }

    private void handleArrayRedisMessage(ArrayRedisMessage arrayRedisMessage, ChannelHandlerContext ctx) {
        List<RedisMessage> redisMessageList = arrayRedisMessage.children();
        if (redisMessageList.size() < 1) {
            ctx.writeAndFlush(RedisResp.invalidCommandRedisResp());
        } else {
            handleRedisCommand(ctx, redisMessageList);
        }
    }

    private void handleRedisCommand(ChannelHandlerContext ctx, List<RedisMessage> redisMessageList) {
        RedisMessage redisMessage = redisMessageList.get(0);
        FullBulkStringRedisMessage stringRedisMessage = (FullBulkStringRedisMessage) redisMessage;
        ByteBuf commandName = stringRedisMessage.content();
        RedisCommand redisCommand = RedisCommand.getCommand(commandName);
        if (redisCommand != null) {
            int size = redisMessageList.size();
            List<RedisMessage> argumentList = redisMessageList.subList(1, size);
            ctx.writeAndFlush(redisCommand.handleCommand(argumentList));
        } else {
            ctx.writeAndFlush(RedisResp.unknownCommandRedisResp());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ServerMonitor.incrTotalChannelCount();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ServerMonitor.decrTotalChannelCount();
    }
}
