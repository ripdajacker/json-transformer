package dk.mehmedbasic.jsonast;

/**
 * A Json value
 */
public class JsonValueNode extends BaseNode {

  private Object value;

  public JsonValueNode(Object value) {
    this.value = value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }

  public void setRawValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public boolean booleanValue() {
    return (boolean) value;
  }

  public double doubleValue() {
    return (double) value;
  }

  public int intValue() {
    return (int) value;
  }

  public String stringValue() {
    return (String) value;
  }

  @Override
  public boolean isBoolean() {
    return value instanceof Boolean;
  }

  @Override
  public boolean isString() {
    return value instanceof String;
  }

  @Override
  public boolean isInt() {
    return value instanceof Integer;
  }

  @Override
  public boolean isDouble() {
    return value instanceof Double;
  }

  @Override
  public boolean isValueNode() {
    return true;
  }

  @Override
  public void addChild(BaseNode node) {
    throw new UnsupportedOperationException("You cannot add a child to a value node.");
  }

  @Override
  public String toString() {
    return "JsonValueNode(" + getValue() + ")";
  }
}
