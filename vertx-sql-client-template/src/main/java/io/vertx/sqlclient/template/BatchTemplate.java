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
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.template.impl.BatchTemplateImpl;
import io.vertx.sqlclient.template.impl.QueryTemplateImpl;
import io.vertx.sqlclient.template.impl.TupleMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@VertxGen
public interface BatchTemplate<T> {

  @GenIgnore
  static BatchTemplate<Map<String, Object>> create(SqlClient client, String template) {
    TupleMapper tupleMapper = new TupleMapper(client, template);
    return new BatchTemplateImpl<>(client, new TupleMapper(client, template), tupleMapper::mapTuple, template);
  }

  static <T> BatchTemplate<T> create(SqlClient client, Function<T, Tuple> mapper, String template) {
    return new BatchTemplateImpl<>(client, new TupleMapper(client, template), mapper, template);
  }

  @GenIgnore
  default Future<Void> batch(List<T> list) {
    Promise<Void> promise = Promise.promise();
    batch(list, promise);
    return promise.future();
  }

  @GenIgnore
  void batch(List<T> list, Handler<AsyncResult<Void>> result);
}
