package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode

/**
 * A node filter that can filter a subtree.
 */
abstract class NodeFilter {
    /**
     * Whether or not the filter accepts the node.
     *
     * @param node the node in question.
     *
     * @return true if accepted, false otherwise.
     */
    abstract boolean apply(BaseNode node, Integer index)

    NodeFilter and(NodeFilter that) {
        return new AndFilter(this, that)
    }

    NodeFilter not() {
        return new NotFilter(this)
    }
}