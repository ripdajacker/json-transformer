package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonArrayNode
import dk.mehmedbasic.jsonast.JsonObjectNode
import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.TypeChecked

/**
 * A function called when values are to be merged.
 */
@TypeChecked
abstract class MergeValueFunction {
    void apply(JsonValueNode source, JsonValueNode destination) {
        // Intentionally left empty
    }

    void apply(JsonObjectNode source, JsonValueNode destination) {
        // Intentionally left empty
    }

    void apply(JsonArrayNode source, JsonValueNode destination) {
        // Intentionally left empty
    }
}