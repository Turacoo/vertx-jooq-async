package io.github.jklingsporn.vertx.jooq.generate.async.classic;

import io.github.jklingsporn.vertx.jooq.generate.async.AbstractVertxGenerator;
import org.jooq.util.JavaWriter;

import java.util.List;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class ClassicAsyncVertxGenerator extends AbstractVertxGenerator {

    public static final String VERTX_DAO_NAME = "io.github.jklingsporn.vertx.jooq.classic.async.VertxDAO";

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import io.vertx.core.Handler;");
        out.println("import io.vertx.core.AsyncResult;");
        out.println("import io.github.jklingsporn.vertx.jooq.future.async.AsyncJooqSQLClient;");
    }

    @Override
    protected void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code> asynchronously", colName);

        out.tab(1).println("public void fetchOneBy%sAsync(%s value,Handler<AsyncResult<%s>> resultHandler) {", colClass, colType,pType);
        out.tab(2).println("vertx().executeBlocking(h->h.complete(fetchOneBy%s(value)),resultHandler);", colClass);
        out.tab(1).println("}");
    }

    @Override
    protected void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public void fetchBy%sAsync(%s<%s> values,Handler<AsyncResult<List<%s>>> resultHandler) {", colClass, List.class, colType,pType);
        //out.tab(2).println("return fetch(%s, values);", colIdentifier);
        out.tab(2).println("fetchAsync(%s,values,resultHandler);", colIdentifier);
        out.tab(1).println("}");
    }
}
