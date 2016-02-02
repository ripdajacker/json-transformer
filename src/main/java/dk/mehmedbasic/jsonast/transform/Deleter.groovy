package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked

/**
 * Deletes child nodes
 */
@TypeChecked
final class Deleter implements TransformStrategy {
    String name
    int index = -1

    Deleter(String name) {
        this.name = name
    }

    Deleter(int index) {
        this.index = index
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        if (name) {
            def nodes = new ArrayList<BaseNode>(root.roots)
            for (BaseNode node : nodes) {
                node.removeNode(name)
            }
        } else if (index >= 0) {
            def nodes = new ArrayList<BaseNode>(root.roots)
            for (BaseNode node : nodes) {
                if (node.isObject() || node.isArray()) {
                    node.removeNode(index)
                }
            }
        }
    }
}
