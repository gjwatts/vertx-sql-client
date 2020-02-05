package examples;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.template.SqlTemplate;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class TemplateExamples {

  static class User {
    public long id;
    public String firstName;
    public String lastName;
  }

  public Function<Row, User> mapper = row -> {
    User user = new User();
    user.id = row.getInteger("id");
    user.firstName = row.getString("firstName");
    user.lastName = row.getString("lastName");
    return user;
  };


  public void fxExample(SqlClient client) {
    SqlTemplate template = SqlTemplate.create(client, "SELECT * FROM users WHERE id=:id");
    template.query(mapper, Collections.singletonMap("id", 1))
      .onSuccess(users -> {
        users.forEach(user -> {
          System.out.println(user.firstName + " " + user.lastName);
        });
      });
  }

  public void bindingExample(SqlClient client) {
    SqlTemplate template = SqlTemplate.create(client, "SELECT * FROM users WHERE id=:id");
    User u = new User();
    u.id = 1;
    template.query(User.class, u)
      .onSuccess(users -> {
        users.forEach(user -> {
          System.out.println(user.firstName + " " + user.lastName);
        });
      });
  }

  public void batchExample(SqlClient client, List<User> users) {
    String sql = "INSERT INTO users (id,first_name,last_name) VALUES (:id,:firstName,:lastName)";
    SqlTemplate template = SqlTemplate.create(client, sql);
    template.batch(User.class, users)
      .onSuccess(v -> {
        System.out.println("Done");
      });
  }
}
