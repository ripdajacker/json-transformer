package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * Renames nodes or their children.
 */
@TypeChecked
@PackageScope
final class Renamer implements TransformStrategy {
    final String to
    final String from

    Renamer(String from, String to) {
        this.from = from
        this.to = to
    }

    Renamer(String to) {
        this(null, to)
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        for (BaseNode node : root.roots) {
            if (from) {
                node.renameNode(from, to)
            } else {
                node.parent?.renameNode(node, to)
            }
        }
    }
}
