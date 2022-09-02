package dk.mehmedbasic.jsonast;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A small identifier class for aiding in CSS query calculation.
 */
public class JsonIdentifier {

  private final Set<String> classes = new LinkedHashSet<>();
  private String name;
  private String id;

  public JsonIdentifier() {
  }

  public JsonIdentifier(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Identifier[" + getName() + ", #" + getId() + ", classes: " + getClasses() + "]";
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

  public Set<String> getClasses() {
    return classes;
  }

  public void addClass(String value) {
    getClasses().add(value);
  }
}
