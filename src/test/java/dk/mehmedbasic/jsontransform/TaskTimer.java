package dk.mehmedbasic.jsontransform;

import dk.mehmedbasic.jsonast.JsonDocument;

/**
 * Times a task
 */
public class TaskTimer {

  public static void timeTaken(String operation, Runnable closure) {
    long start = System.currentTimeMillis();
    closure.run();
    long elapsed = System.currentTimeMillis() - start;
    System.out.println(operation + " took (sec): " + elapsed / 1000d);
  }

  static void timeQuery(String query, JsonDocument jsonDocument) {
    timeTaken("Selected '" + query + '\'', () -> {
      var select = jsonDocument.select(query);
      System.out.println("Query count " + select.getRootCount());
    });
  }
}
