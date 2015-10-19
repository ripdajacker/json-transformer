package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode

/**
 * A node filter
 *
 * @author Jesenko Mehmedbasic
 * created 10/15/2015.
 */
interface NodeFilter {
    boolean apply(BaseNode node)
}