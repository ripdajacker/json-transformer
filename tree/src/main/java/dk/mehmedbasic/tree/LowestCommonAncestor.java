package dk.mehmedbasic.tree;

import java.util.Collections;
import java.util.List;

/**
 * Lca.
 */
public final class LowestCommonAncestor {

  private LowestCommonAncestor() {
  }

  /**
   * Calculates the distance between this nodes and the given node.
   *
   * @param left the node in question.
   * @param right the node in question.
   * @return the number of jumps to the first common ancestor.
   */
  public <T> int distanceBetween(BaseNode<T> left, BaseNode<T> right) {
    List<BaseNode<T>> leftParents = left.pathToRoot();
    if (leftParents.contains(right)) {
      return leftParents.indexOf(right);
    } else {
      List<BaseNode<T>> rightParents = right.pathToRoot();

      Collections.reverse(leftParents);
      Collections.reverse(rightParents);

      int cutoff = Math.min(leftParents.size(), rightParents.size());
      var lca = findDepthOfLca(leftParents, rightParents, cutoff);

      int distanceToRoot = leftParents.size() + rightParents.size();
      int twoLca = 2 * lca;
      return distanceToRoot - twoLca - 1;
    }
  }

  /**
   * Finds the depth from root of the lowest common ancestor.
   *
   * @param leftCutPathToRoot the parent nodes of the left node
   * @param rightCutPathToRoot the parent nodes of the right node
   */
  private static <T> int findDepthOfLca(List<BaseNode<T>> leftCutPathToRoot,
      List<BaseNode<T>> rightCutPathToRoot, int cutoff) {
    for (int i = 0; i < cutoff; i++) {
      var depth = cutoff - i - 1;

      BaseNode<T> a = leftCutPathToRoot.get(depth);
      BaseNode<T> b = rightCutPathToRoot.get(depth);

      if (a != b) {
        return depth - 1;
      }
    }

    return rightCutPathToRoot.size() - 1;
  }
}
