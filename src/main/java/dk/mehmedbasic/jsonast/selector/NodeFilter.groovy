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

    /**
     * Ands the filter with the given filter.
     *
     * @param that the filter.
     *
     * @return a and filter of this and that.
     */
    NodeFilter and(NodeFilter that) {
        return new AndFilter(this, that)
    }

    /**
     * Creates a not filter of this.
     *
     * @return the not filter.
     */
    NodeFilter not() {
        return new NotFilter(this)
    }
}