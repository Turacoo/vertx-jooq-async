package io.github.jklingsporn.vertx.jooq.generate.async.future;

import io.github.jklingsporn.vertx.jooq.generate.async.AbstractVertxGuiceGenerator;
import org.jooq.util.JavaWriter;

import java.util.List;

/**
 * Created by jensklingsporn on 19.04.17.
 */
public class FutureAsyncVertxGuiceGenerator extends AbstractVertxGuiceGenerator {

    public FutureAsyncVertxGuiceGenerator() {
        super(FutureAsyncVertxGenerator.VERTX_DAO_NAME);
    }

    public FutureAsyncVertxGuiceGenerator(boolean generateJson, boolean generateGuiceModules, boolean generateInjectConfigurationMethod) {
        super(FutureAsyncVertxGenerator.VERTX_DAO_NAME, generateJson, generateGuiceModules, generateInjectConfigurationMethod);
    }

    @Override
    protected void generateDAOImports(JavaWriter out) {
        out.println("import java.util.concurrent.CompletableFuture;");
        out.println("import io.github.jklingsporn.vertx.jooq.future.async.impl.FutureTool;");
    }

    @Override
    protected void generateFetchOneByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code> asynchronously", colName);

        out.tab(1).println("public CompletableFuture<%s> fetchOneBy%sAsync(%s value) {", pType,colClass, colType);
        out.tab(2).println("return FutureTool.executeBlocking(h->h.complete(fetchOneBy%s(value)),vertx());", colClass);
        out.tab(1).println("}");
    }

    @Override
    protected void generateFetchByMethods(JavaWriter out, String pType, String colName, String colClass, String colType, String colIdentifier) {
        out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public CompletableFuture<List<%s>> fetchBy%sAsync(%s<%s> values) {", pType, colClass, List.class, colType);
        //out.tab(2).println("return fetch(%s, values);", colIdentifier);
        out.tab(2).println("return fetchAsync(%s,values);", colIdentifier);
        out.tab(1).println("}");
    }
}
