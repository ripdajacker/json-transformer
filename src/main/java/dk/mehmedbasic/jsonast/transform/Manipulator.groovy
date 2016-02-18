package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * Manipulates values
 */
@TypeChecked
@PackageScope
final class Manipulator extends TransformStrategy {
    ManipulateValueFunction function
    Closure closure

    String childName
    int childIndex

    Manipulator(int childIndex, String childName, ManipulateValueFunction function) {
        this.childIndex = childIndex
        this.childName = childName
        this.function = function
    }

    Manipulator(int childIndex, String childName, Closure closure) {
        this.childIndex = childIndex
        this.childName = childName
        this.closure = closure
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        for (BaseNode node : root) {
            if (childIndex >= 0) {
                applyManipulation(node.get(childIndex))
            } else if (childName != null) {
                applyManipulation(node.get(childName))
            } else {
                applyManipulation(node)
            }
        }
    }

    private void applyManipulation(BaseNode node) {
        if (closure) {
            applyClosure(node)
        } else if (function) {
            applyFunction(node)
        }
    }

    private void applyClosure(BaseNode node) {
        if (node.valueNode) {
            closure.call(node as JsonValueNode)
        }
    }

    private void applyFunction(BaseNode node) {
        if (node.valueNode) {
            function.apply(node as JsonValueNode)
        }
    }
}
