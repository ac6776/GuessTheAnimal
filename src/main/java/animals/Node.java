package animals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Node {
    private String val;
    @JsonBackReference
    private Node parent;
    @JsonManagedReference
    private Node yes;
    @JsonManagedReference
    private Node no;

    public  Node() {

    }

    public Node(String val, Node parent) {
        this.val = val;
        this.parent = parent;
    }

    @JsonIgnore
    public boolean isLeaf() {
        return yes == null && no == null;
    }

    @JsonIgnore
    public static Node getRoot(Node node) {
        if (node.parent == null) {
            return node;
        } else {
            return getRoot(node.parent);
        }
    }

    public static List<Node> getLeaves(Node root) {
        List<Node> leaves = new LinkedList<>();
        leave(leaves, root);
        return leaves;
    }

    public static void leave(List<Node> nodes, Node node) {
        if (node.yes == null) {
            nodes.add(node);
            return;
        }
        leave(nodes, node.yes);
        leave(nodes, node.no);
    }

    public static List<Node> getAll(Node root) {
        List<Node> nodes = new LinkedList<>();
        node(nodes, root);
        return nodes;
    }

    private static void node(List<Node> nodes, Node node) {
        if (node == null) {
            return;
        }
        nodes.add(node);
        node(nodes, node.yes);
        node(nodes, node.no);
    }

    public static int height(Node node) {
        if (node == null) {
            return 0;
        }
        return Math.max(1 + height(node.yes), 1 + height(node.no));
    }

    public static int minDepth(Node node) {
        if (node == null) {
            return 0;
        }
        return Math.min(1 + minDepth(node.yes), 1 + minDepth(node.no));
    }

    public static double averageDepth(Node root) {
        double avDepth = 0.0;
        List<Node> leaves = getLeaves(root);
        for (Node leaf : leaves) {
            avDepth += depth(leaf);
        }
        return avDepth / leaves.size();
    }

    private static int depth(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + depth(node.parent);
    }


}
