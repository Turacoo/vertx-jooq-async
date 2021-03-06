package io.github.jklingsporn.vertx.jooq.async.rx;

import io.github.jklingsporn.vertx.jooq.async.rx.util.AsyncJooqSQLClientImpl;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.AsyncSQLClient;
import org.jooq.Query;

import java.util.List;
import java.util.function.Function;

/**
 * Created by jensklingsporn on 13.06.17.
 */
public interface AsyncJooqSQLClient {

    public static AsyncJooqSQLClient create(Vertx vertx, AsyncSQLClient delegate){
        return new AsyncJooqSQLClientImpl(vertx, delegate);
    }

    /**
     * @param query a jOOQ-query
     * @param mapper a function to map the result into another object.
     * @param <P> the type to fetch
     * @return A Single returning a List of P.
     */
    <P> Single<List<P>> fetch(Query query, Function<JsonObject, P> mapper);

    /**
     * @param query a jOOQ-query
     * @param mapper a function to map the result into another object.
     * @param <P> the type to fetch
     * @return A Single returning an object of P or <code>null</code>.
     */
    <P> Single<P> fetchOne(Query query, Function<JsonObject, P> mapper);

    /**
     * @param query a jOOQ-query
     * @return A Single returning the number of affected rows by this query.
     */
    Single<Integer> execute(Query query);

    /**
     * @param query a jOOQ-query to run the insert statement
     * @return A Single returning the lastId returned by mysql.
     */
    Single<Long> insertReturning(Query query);

    /**
     * @return the underlying client
     */
    AsyncSQLClient delegate();
}
