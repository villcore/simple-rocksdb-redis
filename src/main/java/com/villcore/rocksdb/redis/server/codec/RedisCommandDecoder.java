package com.villcore.rocksdb.redis.server.codec;

import com.villcore.rocksdb.redis.server.command.RedisCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ByteProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RedisCommandDecoder extends ReplayingDecoder<Void> {

    /**
     * max allowed request byte size，default 1mb
     */
    private static final int MAX_ALLOWED_REQUEST_BYTE_SIZE = 1 * 1024 * 1024;

    /**
     * RESP protocol type
     */
    private static final byte STRING_PREFIX = '$';
    private static final byte INTEGER_PREFIX = ':';
    private static final byte ERROR_PREFIX = '-';
    private static final byte STATUS_PREFIX = '+';
    private static final byte ARRAY_PREFIX = '*';

    private static final ByteBuf STRING_PREFIX_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{STRING_PREFIX});
    private static final ByteBuf INTEGER_PREFIX_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{INTEGER_PREFIX});
    private static final ByteBuf ERROR_PREFIX_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{ERROR_PREFIX});
    private static final ByteBuf STATUS_PREFIX_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{STATUS_PREFIX});
    private static final ByteBuf ARRAY_PREFIX_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{ARRAY_PREFIX});

    private static final byte CARRIAGE_RETURN = (byte) '\r';
    private static final byte LINE_FEED = (byte) '\n';

    private static final ByteBuf CARRIAGE_RETURN_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{CARRIAGE_RETURN});
    private static final ByteBuf LINE_FEED_BYTE_BUF = Unpooled.wrappedBuffer(new byte[]{LINE_FEED});

    private static final Logger log = LoggerFactory.getLogger(RedisCommandDecoder.class);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(decodeRedisCommand(in));
        checkpoint();
    }

    private ByteBuf[] decodeRedisCommand(ByteBuf in) {
        log.info("Read a redis command {}", in.toString());
        ByteBuf line = readLine(in);
        byte commandStartByte = line.getByte(0);
        if (commandStartByte == ARRAY_PREFIX) {
            log.info("Array");
            int size = Integer.parseInt(line.toString(1, line.writerIndex() - 1, UTF_8));
            return parseArray(in, size);
        } else if (commandStartByte == STRING_PREFIX) {
            log.info("String");
            ByteBuf strBuf = Unpooled.EMPTY_BUFFER;
            int size = Integer.parseInt(line.toString(1, line.writerIndex() - 1, UTF_8));
            if (size > 0 && size <= MAX_ALLOWED_REQUEST_BYTE_SIZE) {
                strBuf = readLine(in);
                log.info("String = {}", strBuf.toString(0, strBuf.writerIndex(), UTF_8));
            }
            return new ByteBuf[]{strBuf};
        }
        return new ByteBuf[]{Unpooled.EMPTY_BUFFER};
        /*
        else if (commandStartByte == STATUS_PREFIX) {
            log.info("Status");
            String statusCommand = line.toString(1, line.writerIndex() - 1, UTF_8);
        } else if (commandStartByte == ERROR_PREFIX) {
            log.info("Error");
            String errorCommand = line.toString(1, line.writerIndex() - 1, UTF_8);
        } else if (commandStartByte == INTEGER_PREFIX) {
            log.info("Integer");
            int integerResult = Integer.parseInt(line.toString(1, line.writerIndex() - 1, UTF_8));
        } else if (commandStartByte == STRING_PREFIX) {
            log.info("String");
            int size = Integer.parseInt(line.toString(1, line.writerIndex() - 1, UTF_8));
            if (size > 0 && size <= MAX_ALLOWED_REQUEST_BYTE_SIZE) {
                ByteBuf str = readLine(in);
                log.info("String = {}", str.toString(0, str.writerIndex(), UTF_8));
            }
            ByteBuf str = null;
        } else {

        }
        */
    }

    private ByteBuf readLine(ByteBuf src) {
        int eol = findNextLinePos(src);
        int readerIndex = src.readerIndex();
        int length = eol - readerIndex;
        src.skipBytes(length + 2);
        return src.slice(readerIndex, length);
    }

    private int findNextLinePos(ByteBuf in) {
        int nextLinePos = in.forEachByte(ByteProcessor.FIND_CRLF);
        if (nextLinePos > 0 && in.getByte(nextLinePos - 1) == CARRIAGE_RETURN) {
            nextLinePos--;
        }
        return nextLinePos;
    }

    private ByteBuf[] parseArray(ByteBuf in, int size) {
        ByteBuf[] byteBuf = new ByteBuf[size];
        for (int i = 0; i < size; i++) {
            byteBuf[i] = decodeRedisCommand(in)[0];
            byteBuf[i].retain();
        }
        return byteBuf;
    }

    public static ByteBuf[] errorResp(ByteBuf byteBuf) {
        return new ByteBuf[]{ERROR_PREFIX_BYTE_BUF.retain(), byteBuf, CARRIAGE_RETURN_BYTE_BUF.retain(), LINE_FEED_BYTE_BUF.retain()};
    }
}
