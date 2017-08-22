package io.github.jklingsporn.vertx.jooq.async.generate.future;

import generated.future.async.vertx.tables.pojos.Something;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jensklingsporn on 22.08.17.
 */
public class JsonConversionTest {

    @Test
    public void convertEmptyPojoToJsonShouldSucceed(){
        Something something = new Something();
        Assert.assertNotNull(something.toJson());
    }

    @Test
    public void convertEmptyJsonToPojoShouldSucceed(){
        Something something = new Something(new JsonObject());
        Assert.assertNotNull(something);
    }

    @Test
    public void convertJsonWithNullValuesToPojoShouldSucceed(){
        Something something = new Something();
        JsonObject jsonObject = something.toJson();
        Assert.assertNotNull(new Something(jsonObject));
    }
}
