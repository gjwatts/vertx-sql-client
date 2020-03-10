package io.vertx.sqlclient.template;

import io.vertx.codegen.annotations.DataObject;

import java.time.LocalDateTime;

@DataObject(generateConverter = true)
public class LocalDateTimeDataObject {

  private LocalDateTime localDateTime;

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }

  public void setLocalDateTime(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }
}
