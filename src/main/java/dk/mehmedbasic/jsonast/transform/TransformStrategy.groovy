package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked

/**
 * Transforms JSON AST according to the implementation.
 */
@TypeChecked
interface TransformStrategy {
    void apply(JsonNodes root)
}
