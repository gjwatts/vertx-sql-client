package io.vertx.sqlclient.template;

/**
 * Mapper for {@link LocalDateTimeDataObject}.
 * NOTE: This class has been automatically generated from the {@link LocalDateTimeDataObject} original class using Vert.x codegen.
 */
public class LocalDateTimeDataObjectMapper {

  public static LocalDateTimeDataObject fromMap(java.util.Map<String, Object> map) {
    LocalDateTimeDataObject obj = new LocalDateTimeDataObject();
    if (map.get("localDateTime") instanceof java.time.LocalDateTime) {
      obj.setLocalDateTime((java.time.LocalDateTime)map.get("localDateTime"));
    }
    return obj;
  }
}
