package dk.mehmedbasic.tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A small identifier class for aiding in CSS query calculation.
 */
public final class NodeId {

  private final Set<String> classes = new LinkedHashSet<>();
  private String name;
  private String id;

  public void addClass(String cssClass) {
    this.classes.add(cssClass);
  }

  public Set<String> getClasses() {
    return classes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
