package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked

/**
 * Transforms JSON AST according to the implementation.
 */
@TypeChecked
abstract class TransformStrategy {
    abstract void apply(JsonDocument document, JsonNodes root)


    static void nodeChanged(JsonNodes nodes, BaseNode baseNode) {
        if (nodes instanceof CachingJsonNodes) {
            nodes.nodeChanged(baseNode)
        }
    }
}
