package io.vertx.sqlclient.template.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.SqlClientBase;
import io.vertx.sqlclient.template.BatchTemplate;
import io.vertx.sqlclient.template.QueryTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BatchTemplateImpl<T> implements BatchTemplate<T> {

  private static Pattern P = Pattern.compile(":(\\p{Alnum}+)");

  private final SqlClientBase<?> client;
  private final TupleMapper tupleMapper;
  private final Function<T, Tuple> mapper;

  public BatchTemplateImpl(SqlClient client, TupleMapper mapper2, Function<T, Tuple> mapper, String template) {
    this.mapper = mapper;
    this.tupleMapper = mapper2;
    this.client = (SqlClientBase) client;
  }

  @Override
  public void batch(List<T> list, Handler<AsyncResult<Void>> result) {
    client.preparedBatch(tupleMapper.sql, list
      .stream()
      .map(mapper)
      .collect(Collectors.toList()), ar -> result.handle(ar.mapEmpty()));
  }
}
