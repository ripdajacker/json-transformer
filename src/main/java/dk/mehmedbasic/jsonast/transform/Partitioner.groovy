package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked

/**
 * Partitions a node given a list of List<String>.
 */
@TypeChecked
final class Partitioner implements TransformStrategy {
    private List<Tuple2<String, List<String>>> partitionKeys = []

    Partitioner(List<List<String>> partitionKeys) {
        for (List<String> keys : partitionKeys) {
            if (keys.size() >= 2) {
                def tuple = new Tuple2<>(keys.get(0), keys.tail())
                this.partitionKeys.add(tuple)
            }
        }

    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        for (BaseNode source : root.roots) {
            for (Tuple2<String, List<String>> partition : partitionKeys) {
                def keys = partition.second
                if (source.isObject()) {
                    if (source.parent) {
                        def destination = document.createObjectNode()
                        def newKey = partition.first
                        destination.identifier.name = newKey
                        destination.identifier.classes << "sysclass_partitioned"

                        source.parent.addChild(destination)

                        for (String key : keys) {
                            BaseNode node = source.get(key)
                            if (!node) {
                                throw new IllegalArgumentException("The given node was not found $key")
                            }
                            source.removeNode(node as BaseNode)

                            destination.addChild(node)
                        }
                    }
                }
            }
        }
    }
}
