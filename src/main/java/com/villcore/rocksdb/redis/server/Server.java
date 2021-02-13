package com.villcore.rocksdb.redis.server;

import com.villcore.rocksdb.redis.common.NamedThreadFactory;
import com.villcore.rocksdb.redis.server.codec.RedisCommandDecoder;
import com.villcore.rocksdb.redis.server.codec.RedisCommandEncoder;
import com.villcore.rocksdb.redis.server.codec.RedisCommandHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);
    public static final int MAX_BUFFER_SIZE = 128 * 1024;

    private ServerConfig serverConfig;

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;
    private ChannelFuture serverSocketChannelFuture;

    public Server(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startup() {
        init();
        log.info("Server startup complete");
    }

    public void shutdown() {
        log.info("Server shutdown");
        if (serverSocketChannelFuture != null) {
            serverSocketChannelFuture.channel().close().awaitUninterruptibly();
        }
        if (bossEventLoopGroup != null) {
            bossEventLoopGroup.shutdownGracefully();
        }
        log.info("Server shutdown complete");
    }

    private void init() {

        // TODO: use env config event loop, maybe use epoll event loop
        bossEventLoopGroup = new NioEventLoopGroup(1,
                new NamedThreadFactory("server-boss-thread", false));
        workerEventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,
                new NamedThreadFactory("server-worker-thread", true));

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_SNDBUF, 128 * 1024)
                .option(ChannelOption.SO_RCVBUF, MAX_BUFFER_SIZE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RedisCommandEncoder())
                                .addLast(new RedisCommandDecoder())
                                .addLast(new RedisCommandHandler());
                    }
                });
        serverSocketChannelFuture = serverBootstrap.bind(serverConfig.getPort()).syncUninterruptibly();
    }
}
