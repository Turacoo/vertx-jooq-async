package io.github.jklingsporn.vertx.jooq.async.classic;

import io.github.jklingsporn.vertx.jooq.async.shared.VertxPojo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Created by jensklingsporn on 21.10.16.
 * Vertx-ified version of jOOQs <code>DAO</code>-interface.
 */
public interface VertxDAO<R extends UpdatableRecord<R>, P extends VertxPojo, T> extends DAO<R, P, T> {

    AsyncJooqSQLClient client();

    void setClient(AsyncJooqSQLClient client);

    /**
     * @return a function that maps a <code>JsonObject</code> to a Pojo. Usually just the constructor.
     */
    Function<JsonObject, P> jsonMapper();

    /**
     * Checks if a given ID exists asynchronously
     *
     * @param id The ID whose existence is checked
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #existsById(Object)
     */
    default void existsByIdAsync(T id, Handler<AsyncResult<Boolean>> resultHandler){
        findByIdAsync(id, h -> {
            if (h.succeeded()) {
                resultHandler.handle(Future.succeededFuture(h.result() != null));
            }else{
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    /**
     * Count all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #count()
     */
    default void countAsync(Handler<AsyncResult<Long>> resultHandler){
        client().fetchOne(DSL.using(configuration()).selectCount().from(getTable()),
                json -> json.getMap().values().stream().findFirst(), h -> {
                    if (h.succeeded()) {
                        resultHandler.handle(Future.succeededFuture((Long) h.result().get()));
                    } else {
                        resultHandler.handle(Future.failedFuture(h.cause()));
                    }
                });
    }

    /**
     * Find all records of the underlying table asynchronously.
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findAll()
     */
    default void findAllAsync(Handler<AsyncResult<List<P>>> resultHandler){
        fetchAsync(DSL.trueCondition(),resultHandler);
    }

    /**
     * Find a record of the underlying table by ID asynchronously.
     *
     * @param id The ID of a record in the underlying table
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #findById(Object)
     */
    default void findByIdAsync(T id, Handler<AsyncResult<P>> resultHandler){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }
        else {
            condition = row(pk).equal((Record) id);
        }
        fetchOneAsync(condition,resultHandler);
    }

    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOne(Field, Object)
     */
    default <Z> void fetchOneAsync(Field<Z> field, Z value, Handler<AsyncResult<P>> resultHandler){
        fetchOneAsync(field.eq(value),resultHandler);
    }

    /**
     * Find a unique record by a given condition asynchronously.
     *
     * @param condition the condition to fetch one value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void fetchOneAsync(Condition condition, Handler<AsyncResult<P>> resultHandler){
        client().fetchOne(DSL.using(configuration()).selectFrom(getTable()).where(condition), jsonMapper(),resultHandler);
    }


    /**
     * Find a unique record by a given field and a value asynchronously.
     *
     * @param field The field to compare value against
     * @param value The accepted value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     * @see #fetchOptional(Field, Object)
     */
    default <Z> void fetchOptionalAsync(Field<Z> field, Z value, Handler<AsyncResult<Optional<P>>> resultHandler){
        fetchOneAsync(field,value,h->{
            if(h.succeeded()){
                resultHandler.handle(Future.succeededFuture(Optional.ofNullable(h.result())));
            }else{
                resultHandler.handle(Future.failedFuture(h.cause()));
            }
        });
    }

    /**
     * Find records by a given field and a set of values asynchronously.
     *
     * @param field The field to compare values against
     * @param values The accepted values
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void fetchAsync(Field<Z> field, Collection<Z> values, Handler<AsyncResult<List<P>>> resultHandler){
        fetchAsync(field.in(values),resultHandler);
    }

    /**
     * Find records by a given condition asynchronously.
     *
     * @param condition the condition to fetch one value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default void fetchAsync(Condition condition, Handler<AsyncResult<List<P>>> resultHandler){
        client().fetch(DSL.using(configuration()).selectFrom(getTable()).where(condition), jsonMapper(),resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given key and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param id The key to be deleted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    @SuppressWarnings("unchecked")
    default void deleteExecAsync(T id, Handler<AsyncResult<Integer>> resultHandler){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }
        else {
            condition = row(pk).equal((Record) id);
        }
        deleteExecAsync(condition,resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param condition The condition for the delete query
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void deleteExecAsync(Condition condition, Handler<AsyncResult<Integer>> resultHandler ){
        client().execute(DSL.using(configuration()).deleteFrom(getTable()).where(condition),resultHandler);
    }

    /**
     * Performs an async <code>DELETE</code> statement for a given condition and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param field the field
     * @param value the value
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default <Z> void deleteExecAsync(Field<Z> field, Z value, Handler<AsyncResult<Integer>> resultHandler){
        deleteExecAsync(field.eq(value),resultHandler);
    }

    /**
     * Performs an async <code>UPDATE</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be updated
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default void updateExecAsync(P object, Handler<AsyncResult<Integer>> resultHandler){
        DSLContext dslContext = DSL.using(configuration());
        client().execute(dslContext.update(getTable()).set(dslContext.newRecord(getTable(), object)),resultHandler);
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the number of affected rows
     * to the <code>resultHandler</code>.
     * @param object The POJO to be inserted
     * @param resultHandler the resultHandler which succeeds when the blocking method of this type succeeds or fails
     *                      with an <code>DataAccessException</code> if the blocking method of this type throws an exception
     */
    default void insertExecAsync(P object, Handler<AsyncResult<Integer>> resultHandler){
        client().execute(DSL.using(configuration()).insertInto(getTable()).values(object.toJson().getMap().values()),resultHandler);
    }

    /**
     * Performs an async <code>INSERT</code> statement for a given POJO and passes the primary key
     * to the <code>resultHandler</code>. When the value could not be inserted, the <code>resultHandler</code>
     * will fail.
     * @param object The POJO to be inserted
     * @param resultHandler the resultHandler
     */
    @SuppressWarnings("unchecked")
    default void insertReturningPrimaryAsync(P object, Handler<AsyncResult<T>> resultHandler){
        throw new UnsupportedOperationException(":(");
//        UniqueKey<?> key = getTable().getPrimaryKey();
//        //usually key shouldn't be null because DAO generation is omitted in such cases
//        Objects.requireNonNull(key,()->"No primary key");
//        executeAsync(dslContext -> {
//            R record = dslContext.insertInto(getTable()).set(dslContext.newRecord(getTable(), object)).returning(key.getFields()).fetchOne();
//            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
//            Record key1 = record.key();
//            if(key1.size() == 1){
//                return ((Record1<T>)key1).value1();
//            }
//            return (T) key1;
//        }, resultHandler);
    }

}
