package io.github.jklingsporn.vertx.jooq.generate.async.classic;

import io.github.jklingsporn.vertx.jooq.generate.async.TestTool;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by jklingsporn on 17.09.16.
 */
public class VertxGuiceGeneratorTest {

    @BeforeClass
    public static void createTestSchema() throws SQLException {
        TestTool.setupDB();
    }

    @Test
    public void generateCodeShouldSucceed() throws Exception {

        Configuration configuration = TestTool.createGeneratorConfig(
                ClassicAsyncVertxGuiceGenerator.class.getName(),"classic.guice",  ClassicAsyncGeneratorStrategy.class);

        try {
            GenerationTool.generate(configuration);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
