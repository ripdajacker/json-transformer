package dk.mehmedbasic.jsonast.transform

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

    Transformer add(Object value) {
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

    void apply() {
        if (destination) {
            for (TransformStrategy function : functions) {
                function.apply(destination.document, destination.select(selector))
            }
            destination.treeChanged()
        }

    }
}
