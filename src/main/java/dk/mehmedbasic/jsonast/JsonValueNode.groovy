package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * A Json value
 */
@TypeChecked
class JsonValueNode extends BaseNode {
    private Object value

    JsonValueNode() {
    }

    JsonValueNode(Object value) {
        this.value = value
    }

    void setValue(String value) {
        this.value = value
    }

    void setValue(int value) {
        this.value = value
    }

    void setValue(double value) {
        this.value = value
    }

    void setValue(boolean value) {
        this.value = value
    }

    Object getValue() {
        value
    }

    boolean booleanValue() {
        return value as boolean
    }

    double doubleValue() {
        return value as double
    }

    int intValue() {
        return value as int
    }

    String stringValue() {
        return value as String
    }

    @Override
    boolean isBoolean() {
        return value instanceof Boolean
    }

    @Override
    boolean isString() {
        return value instanceof String
    }

    @Override
    boolean isInt() {
        return value instanceof Integer
    }

    @Override
    boolean isDouble() {
        return value instanceof Double
    }

    @Override
    boolean isValueNode() {
        true
    }

    @Override
    void addChild(BaseNode node) {
        throw new UnsupportedOperationException("You cannot add a child to a value node.")
    }

    @Override
    public String toString() {
        return "JsonValueNode($value)"
    }
}
