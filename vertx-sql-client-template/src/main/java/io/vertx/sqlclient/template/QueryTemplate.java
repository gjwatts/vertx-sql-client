package io.vertx.sqlclient.template;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.template.impl.QueryTemplateImpl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@VertxGen
public interface QueryTemplate<T> {

  @GenIgnore
  static QueryTemplate<Row> create(SqlClient client, String template) {
    return new QueryTemplateImpl<>(client, Function.identity(), template);
  }

  static <T> QueryTemplate<T> create(SqlClient client, Function<Row, T> mapper, String template) {
    return new QueryTemplateImpl<>(client, mapper, template);
  }

  static <T> QueryTemplate<T> create(SqlClient client, Class<T> type, String template) {
    return new QueryTemplateImpl<>(client, row -> {
      JsonObject json = new JsonObject();
      for (int i = 0;i < row.size();i++) {
        json.getMap().put(row.getColumnName(i), row.getValue(i));
      }
      return json.mapTo(type);
    }, template);
  }

  @GenIgnore
  default Future<List<T>> query(Object args) {
    Promise<List<T>> promise = Promise.promise();
    query(args, promise);
    return promise.future();
  }

  @GenIgnore
  default Future<List<T>> query(Map<String, Object> args) {
    Promise<List<T>> promise = Promise.promise();
    query(args, promise);
    return promise.future();
  }

  @GenIgnore
  default void query(Object args, Handler<AsyncResult<List<T>>> resultHandler) {
    query(JsonObject.mapFrom(args).getMap(), resultHandler);
  }

  @GenIgnore
  void query(Map<String, Object> args, Handler<AsyncResult<List<T>>> resultHandler);

}
