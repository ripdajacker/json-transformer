package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode

/**
 * A node filter that can filter a subtree.
 */
interface NodeFilter {
    /**
     * Whether or not the filter accepts the node.
     *
     * @param node the node in question.
     *
     * @return true if accepted, false otherwise.
     */
    boolean apply(BaseNode node)
}