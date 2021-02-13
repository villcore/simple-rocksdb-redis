package com.villcore.rocksdb.redis.server.command;

import com.villcore.rocksdb.redis.server.command.string.SetCommand;
import com.villcore.rocksdb.redis.store.DataStore;

public class CommandInitializer {

    private DataStore dataStore;

    public CommandInitializer(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void initializeCommand() {
        RedisCommand.registerCommand(new SetCommand(dataStore));
    }
}
