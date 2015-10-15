package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked

/**
 * Deletes child nodes
 */
@TypeChecked
class Deleter implements TransformStrategy {
    String name
    int index

    Deleter(String name) {
        this.name = name
    }

    Deleter(int index) {
        this.index = index
    }

    @Override
    void apply(JsonNodes root) {
        for (BaseNode node : root.roots) {
            node.removeNode(name)
        }
    }
}
