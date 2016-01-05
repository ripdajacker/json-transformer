package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.JsonType
import groovy.transform.TypeChecked

/**
 * A json transformer builder.
 *
 * This small class builds and executes transformation functions.
 */
@TypeChecked
final class Transformer {


    List<TransformStrategy> functions = []
    String selector
    JsonNodes destination

    Transformer(String selector, JsonNodes destination) {
        this.selector = selector
        this.destination = destination
    }

    Transformer renameTo(String newName) {
        functions << new Renamer(newName)
        this
    }

    Transformer renameChild(String from, String to) {
        functions << new Renamer(from, to)
        this
    }

    Transformer moveTo(String selector) {
        functions << new Mover(selector)
        this
    }

    Transformer merge(String selector) {
        merge(selector, null)
    }

    Transformer deleteChild(String childName) {
        functions << new Deleter(childName)
        this
    }

    Transformer deleteChild(int index) {
        functions << new Deleter(index)
        this
    }

    Transformer merge(String selector, MergeValueFunction function) {
        functions << new Merger(selector, function)
        this
    }

    Transformer manipulateValue(ManipulateValueFunction function) {
        functions << new Manipulator(function)
        this
    }

    Transformer add(String name, JsonType type) {
        functions << new AddValue(name, type, null)
        this
    }

    Transformer add(String name, JsonType type, Object value) {
        functions << new AddValue(name, type, value)
        this
    }

    Transformer addJson(String name, String jsonString) {
        def parsed = JsonDocument.parse(jsonString)
        if (parsed.roots.isEmpty()) {
            return this
        }

        def root = parsed.roots.get(0)
        functions << new AddValue(name, JsonType.fromNode(root), root)
        this
    }

    Transformer addValue(Object value) {
        functions << new AddValue(null, JsonType.Value, value)
        this
    }

    Transformer add(String name, Object value) {
        functions << new AddValue(name, JsonType.Value, value)
        this
    }

    Transformer partition(List<List<String>> partitions) {
        functions << new Partitioner(partitions)
        this
    }

    JsonNodes apply() {
        if (destination) {
            for (TransformStrategy function : functions) {
                function.apply(destination.document, destination.select(selector))
            }
            destination.treeChanged()
        }

        return destination
    }
}
