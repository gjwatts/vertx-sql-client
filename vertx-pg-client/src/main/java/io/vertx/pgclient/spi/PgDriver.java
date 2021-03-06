package io.vertx.pgclient.spi;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.spi.Driver;

public class PgDriver implements Driver {

  @Override
  public Pool createPool(SqlConnectOptions options, PoolOptions poolOptions) {
    return PgPool.pool(wrap(options), poolOptions);
  }

  @Override
  public Pool createPool(Vertx vertx, SqlConnectOptions options, PoolOptions poolOptions) {
    return PgPool.pool(vertx, wrap(options), poolOptions);
  }

  @Override
  public boolean acceptsOptions(SqlConnectOptions options) {
    return options instanceof PgConnectOptions || SqlConnectOptions.class.equals(options.getClass());
  }
  
  private static PgConnectOptions wrap(SqlConnectOptions options) {
    if (options instanceof PgConnectOptions) {
      return (PgConnectOptions) options; 
    } else {
      return new PgConnectOptions(options);
    }
  }

}
