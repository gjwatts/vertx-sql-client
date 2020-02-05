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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.pgclient.PgClientTestBase;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgConnectionTestBase;
import io.vertx.pgclient.PgTestBase;
import io.vertx.sqlclient.SqlConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
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
      SqlTemplate template = SqlTemplate.create(conn, "SELECT id, randomnumber from WORLD WHERE id=:id");
      template.query(Function.identity(), Collections.singletonMap("id", 1), ctx.asyncAssertSuccess(res -> {
        System.out.println("DONE");
      }));
    }));
  }

  @Test
  public void testQueryMap(TestContext ctx) {
    World w = new World();
    w.id = 1;
    connector.accept(ctx.asyncAssertSuccess(conn -> {
      SqlTemplate template = SqlTemplate.create(conn, "SELECT id, randomnumber from WORLD WHERE id=:id");
      template.query(World.class, w, ctx.asyncAssertSuccess(res -> {
        res.forEach(world -> {
          System.out.println(world.id + " " + world.randomnumber);
        });
      }));
    }));
  }
}
