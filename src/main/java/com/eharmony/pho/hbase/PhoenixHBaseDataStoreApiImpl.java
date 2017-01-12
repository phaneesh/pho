package com.eharmony.pho.hbase;

import com.eharmony.pho.api.DataStoreApi;
import com.eharmony.pho.hbase.query.PhoenixHBaseQueryExecutor;
import com.eharmony.pho.hbase.util.PhoenixConnectionManager;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.builder.QueryBuilder;
import com.eharmony.pho.query.builder.QueryUpdateBuilder;
import com.google.common.base.Preconditions;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Datastore api implementation for HBase store. Using apache phoenix (http://phoenix.apache.org/) as sql layer to hbase
 *
 * @author vvangapandu
 */
public class PhoenixHBaseDataStoreApiImpl implements DataStoreApi {

    private final PhoenixHBaseQueryExecutor queryExecutor;
    private static final Logger logger = LoggerFactory.getLogger(PhoenixHBaseDataStoreApiImpl.class);

    private final Connection connection;

    public PhoenixHBaseDataStoreApiImpl(final String connectionUrl, final PhoenixHBaseQueryExecutor queryExecutor)
            throws Exception {
        this.queryExecutor = Preconditions.checkNotNull(queryExecutor);
        // Below code will ensure that connection is string is valid, if not will stop the context loading
        connection = PhoenixConnectionManager.getConnection(connectionUrl);
        if (connection == null) {
            throw new IllegalStateException("unable to create phoenix connection with given url :" + connectionUrl);
        }
    }

    public Connection getConnection() {
        Preconditions.checkNotNull(connection);
        return connection;
    }

    @Override
    public <K> K executeCustomQuery(String query, ResultSetHandler<K> resultSetHandler, Object... parameters) {
        try {
            QueryRunner runner = new QueryRunner();
            return runner.query(connection, query, resultSetHandler, parameters);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public <T> T save(T entity) {
        try {
            T returnEntity = queryExecutor.save(entity, connection);
            connection.commit();
            return returnEntity;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public <T> Iterable<T> save(Iterable<T> entities) {
        try {
            Iterable<T> results = queryExecutor.save(entities, connection);
            connection.commit();
            return results;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public <T> int[] saveBatch(Iterable<T> entities) {
        try {
            int[] results = queryExecutor.saveBatch(entities, connection);
            connection.commit();
            return results;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public <T, R> Iterable<R> findAll(QuerySelect<T, R> query) {
        try {
            return queryExecutor.find(query, connection);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("resource")
    public <T, R> R findOne(QuerySelect<T, R> query) {
        try {
            return queryExecutor.findOne(query, connection);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("resource")
    public <T> Iterable<T> findAllEntities(String key, Class<T> clz, String[] projection) throws Exception {
        try {
            QueryBuilder<T, T> builder = new QueryBuilder<T, T>(clz, clz);
            builder.setReturnFields(projection);
            QuerySelect<T, T> query = builder.build();
            return queryExecutor.find(query, connection);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> T save(T entity, List<String> selectedFields) {
        try {
            QueryUpdateBuilder updateBuilder = QueryUpdateBuilder.builderFor(entity).update(selectedFields);
            T returnEntity = (T) queryExecutor.save(updateBuilder.build(), connection);
            connection.commit();
            return returnEntity;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }



}
