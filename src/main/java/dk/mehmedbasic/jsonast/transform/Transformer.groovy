package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonNodes
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

    Transformer(String selector) {
        this.selector = selector
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

    void apply(JsonNodes roots) {
        for (TransformStrategy function : functions) {
            function.apply(roots.select(selector))
        }
        roots.treeChanged()
    }
}
