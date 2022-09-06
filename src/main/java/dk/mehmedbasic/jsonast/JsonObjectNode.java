package dk.mehmedbasic.jsonast;

import java.util.ArrayList;
import java.util.List;

/**
 * A Json object node.
 */
public class JsonObjectNode extends BaseNode {

    private final List<BaseNode> children = new ArrayList<>();

    public JsonObjectNode() {
    }

    @Override
    public BaseNode get(final String name) {
        return children.stream().filter(it -> it.getIdentifier().getName().equals(name))
            .findAny().orElse(null);
    }

    @Override
    public void addChild(BaseNode node) {
        super.addChild(node);
        children.add(node);
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    public int size() {
        return children.size();
    }

    @Override
    public void removeNode(BaseNode node) {
        if (children.contains(node)) {
            children.remove(node);
            super.removeNode(node);
            node.setParent(null);
        }
    }

    @Override
    public String toString() {
        return "JsonObjectNode[" + getIdentifier() + "]{" + getChildren() + "}";
    }

    public List<BaseNode> getChildren() {
        return children;
    }
}
