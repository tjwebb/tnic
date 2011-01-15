package com.tnic.db;

import com.tnic.config.InvalidConfigurationException;
import com.tnic.config.Env;

/**
 * Provide a persistence layer that is appropriate for the current system
 * configuration
 */
public class DatastoreFactory {
    public static Datastore getDatastore () throws InvalidConfigurationException {
        if (Env.PERSISTENCE_PROVIDER == null) {
            throw new InvalidConfigurationException("PERSISTENCE_PROVIDER not set");
        }

        String p = Env.PERSISTENCE_PROVIDER;
        if ("Google".equals(p)) {
            return new AppEngineDatastore();
        }
        else {
            if (Env.JDBC_DRIVER == null) {
                throw new InvalidConfigurationException("JDBC_DRIVER not set");
            }
            return new JdbcDatastore(Env.JDBC_DRIVER);
        }
    }
}
