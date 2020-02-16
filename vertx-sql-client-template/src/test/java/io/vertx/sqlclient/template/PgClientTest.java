/*
 * Copyright (C) 2017 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.vertx.sqlclient.template;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.unit.TestContext;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgTestBase;
import io.vertx.sqlclient.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class PgClientTest extends PgTestBase {

  protected Vertx vertx;
  protected Consumer<Handler<AsyncResult<PgConnection>>> connector;

  @Before
  public void setup() throws Exception {
    super.setup();
    vertx = Vertx.vertx();
  }

  @After
  public void teardown(TestContext ctx) {
    vertx.close(ctx.asyncAssertSuccess());
  }

  public PgClientTest() {
    connector = (handler) -> PgConnection.connect(vertx, options, ar -> {
      handler.handle(ar.map(p -> p));
    });
  }

  @Test
  public void testQuery(TestContext ctx) {
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      QueryTemplate<Row> template = QueryTemplate.create(conn, "SELECT id, randomnumber from WORLD WHERE id=:id");
      template.query( Collections.singletonMap("id", 1), ctx.asyncAssertSuccess(res -> {
        System.out.println("DONE");
      }));
    }));
  }

  @Test
  public void testQueryMap(TestContext ctx) {
    World w = new World();
    w.id = 1;
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      QueryTemplate<World> template = QueryTemplate.create(conn, World.class, "SELECT id, randomnumber from WORLD WHERE id=:id");
      template.query(w, ctx.asyncAssertSuccess(res -> {
        res.forEach(world -> {
          System.out.println(world.id + " " + world.randomnumber);
        });
      }));
    }));
  }

  @Test
  public void testLocalDateTimeWithJackson(TestContext ctx) {
    DatabindCodec.mapper().registerModule(new JavaTimeModule());
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      LocalDateTime ldt = LocalDateTime.parse("2017-05-14T19:35:58.237666");
      QueryTemplate<LocalDateTimePojo> template = QueryTemplate.create(conn, LocalDateTimePojo.class, "SELECT :value :: TIMESTAMP WITHOUT TIME ZONE \"localDateTime\"");
      template.query(Collections.singletonMap("value", ldt), ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        ctx.assertEquals(ldt, result.get(0).localDateTime);
      }));
    }));
  }

  @Test
  public void testLocalDateTimeWithCodegen(TestContext ctx) {
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      LocalDateTime ldt = LocalDateTime.parse("2017-05-14T19:35:58.237666");
      Function<Row, LocalDateTimeDataObject> mapper = row -> {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0;i < row.size();i++) {
          map.put(row.getColumnName(i), row.getValue(i));
        }
        return LocalDateTimeDataObjectConverter.fromMap(map.entrySet());
      };
      QueryTemplate<LocalDateTimeDataObject> template = QueryTemplate.create(conn, mapper, "SELECT :value :: TIMESTAMP WITHOUT TIME ZONE \"localDateTime\"");
      template.query(Collections.singletonMap("value", ldt), ctx.asyncAssertSuccess(result -> {
        ctx.assertEquals(1, result.size());
        ctx.assertEquals(ldt, result.get(0).getLocalDateTime());
      }));
    }));
  }

  @Test
  public void testBatchUpdate(TestContext ctx) {
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      BatchTemplate<World> template = BatchTemplate.create(conn, World.class, "INSERT INTO World (id, randomnumber) VALUES (:id, :randomnumber)");
      template.batch(Arrays.asList(
        new World(20_000, 0),
        new World(20_001, 1),
        new World(20_002, 2),
        new World(20_003, 3)
      ), ctx.asyncAssertSuccess(v -> {
        conn.query("SELECT id, randomnumber from WORLD WHERE id=20000", ctx.asyncAssertSuccess(rowset -> {
          ctx.assertEquals(1, rowset.size());
          ctx.assertEquals(0, rowset.iterator().next().getInteger(1));
        }));
      }));
    }));
  }
}
