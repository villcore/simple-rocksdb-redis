package com.villcore.rocksdb.redis.server.command;

import io.netty.buffer.ByteBuf;

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
}
