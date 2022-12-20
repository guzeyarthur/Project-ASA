package com.projectASA.cache;

import com.projectASA.services.IDBFactory;

public class FactoryCache {
    private static final String RESPONSE_BINARY = "binary";
    private static final String RESPONSE_CSV = "csv";
    private static final String RESPONSE_JSON = "json";
    private static final String RESPONSE_XML = "xml";
    private static final String RESPONSE_YAML = "yaml";
    private static final String RESPONSE_MY_SQL = "mySql";
    private static final String RESPONSE_POSTGRE_SQL = "postgreSql";
    private static final String RESPONSE_MONGO_DB = "mongoDB";
    private static final String RESPONSE_CASSANDRA = "cassandra";
    private static final String RESPONSE_H2 = "h2";
    private static final String RESPONSE_REDIS = "redis";
    private static final String RESPONSE_GRAPH_DB = "graphDB";


    private IDBFactory[] capacity;

    public FactoryCache(IDBFactory[] capacity) {
        this.capacity = capacity;
    }

    public IDBFactory getEnvironment (String factory) {
        switch(factory) {
            case RESPONSE_BINARY: return capacity[0];
            case RESPONSE_CSV: return capacity[1];
            case RESPONSE_JSON: return capacity[2];
            case RESPONSE_XML: return capacity[3];
            case RESPONSE_YAML: return capacity[4];
            case RESPONSE_MY_SQL: return capacity[5];
            case RESPONSE_POSTGRE_SQL: return capacity[6];
            case RESPONSE_MONGO_DB: return capacity[7];
            case RESPONSE_CASSANDRA: return capacity[8];
            case RESPONSE_H2: return capacity[9];
            case RESPONSE_REDIS: return capacity[10];
            case RESPONSE_GRAPH_DB: return capacity[11];
            default: return null;
        }
    }
}
