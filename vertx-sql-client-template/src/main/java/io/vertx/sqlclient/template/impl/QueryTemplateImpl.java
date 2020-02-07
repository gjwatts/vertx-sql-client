package io.vertx.sqlclient.template.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.SqlClientBase;
import io.vertx.sqlclient.template.QueryTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTemplateImpl<T> implements QueryTemplate<T> {

  private static Pattern P = Pattern.compile(":(\\p{Alnum}+)");

  private final SqlClientBase<?> client;
  private final TupleMapper tupleMapper;
  private final Function<Row, T> mapper;

  public QueryTemplateImpl(SqlClient client, Function<Row, T> mapper, String template) {

    this.mapper = mapper;
    this.tupleMapper = new TupleMapper(client, template);
    this.client = (SqlClientBase) client;
  }

  @Override
  public void query(Map<String, Object> args, Handler<AsyncResult<List<T>>> asyncResultHandler) {
    Tuple tuple = tupleMapper.mapTuple(args);
    client.preparedQuery(tupleMapper.sql, tuple, ar -> {
      asyncResultHandler.handle(ar.map(abc -> {
        List<T> list = new ArrayList<>();
        abc.forEach(r -> list.add(mapper.apply(r)));
        return list;
      }));
    });
  }
}
