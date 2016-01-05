package dk.mehmedbasic.jsonast

/**
 * The Json type enum.
 */
enum JsonType {
    Value, Array, Object

    static JsonType fromNode(BaseNode node) {
        if (node.isArray()) {
            return Array
        }
        if (node.isObject()) {
            return Object
        }
        if (node.isValueNode()) {
            return Value
        }
        return null
    }
}