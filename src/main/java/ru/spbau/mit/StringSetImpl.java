package ru.spbau.mit;

/**
 * Created by idmit on 17/02/2016.
 */
public class StringSetImpl implements StringSet {
    /**
     * Size of ASCII table range from `A` to the `z`.
     */
    private static final int MAX_CHILDREN = 'z' - 'A' + 1;

    /**
     * An offset in the ASCII table from `\0` to `A`.
     */
    private static final int ASCII_OFFSET = 'A';

    private Node root;
    private int size;

    /**
     * Node is a building block of any trie. It has children, a flag and number of children that are elements.
     * Flag shows if this node is an end of some set element or not.
     */
    private static class Node {
        private Node[] children = new Node[MAX_CHILDREN];
        private boolean isElement;
        private int numberOfElementChildren;

        public Node getChild(char letter) {
            return children[letter - ASCII_OFFSET];
        }

        public void setChild(char letter, Node node) {
            children[letter - ASCII_OFFSET] = node;
        }
    }

    /**
     * This compound type stores target node of some operation and a flag.
     * Flag shows if this node was affected by operation in question.
     */
    private static class OperationResult {
        private Node node;
        private boolean changesWereApplied;

        OperationResult(Node node) {
            this.node = node;
        }
    }

    /**
     * This recursive subroutine goes along the path to node corresponding to given element.
     * If some nodes on that path are non-existent they are created.
     *
     * @param x
     * @param element
     * @param depth
     * @return
     */
    private OperationResult add(Node x, String element, int depth) {
        if (x == null) {
            x = new Node();
        }

        OperationResult opRes = new OperationResult(x);

        if (depth == element.length()) {
            if (!x.isElement) {
                size++;
                x.isElement = true;
                opRes.changesWereApplied = true;
            }
        } else {
            char c = element.charAt(depth);
            OperationResult recRes = add(x.getChild(c), element, depth + 1);
            x.setChild(c, recRes.node);
            if (recRes.changesWereApplied) {
                x.numberOfElementChildren += 1;
            }
            opRes.changesWereApplied = recRes.changesWereApplied;
        }
        return opRes;
    }

    /**
     * Expected complexity: O(|element|)
     *
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
    @Override
    public boolean add(String element) {
        OperationResult opRes = add(root, element, 0);
        root = opRes.node;
        return opRes.changesWereApplied;
    }

    /**
     * This recursive subroutine goes along the path to node corresponding to given element.
     *
     * @param x
     * @param element
     * @param depth
     * @return
     */
    private Node getEndNode(Node x, String element, int depth) {
        if (x == null) {
            return null;
        }
        if (depth == element.length()) {
            return x;
        }
        char c = element.charAt(depth);
        return getEndNode(x.getChild(c), element, depth + 1);
    }

    /**
     * Expected complexity: O(|element|)
     */
    @Override
    public boolean contains(String element) {
        Node x = getEndNode(root, element, 0);
        return x != null && x.isElement;
    }

    /**
     * This recursive subroutine goes along the path to node corresponding to given element,
     * and, if that path is complete, that element is deleted.
     * Backward recursion steps remove subtries that don't contain any elements.
     *
     * @param x
     * @param element
     * @param depth
     * @return
     */
    private OperationResult delete(Node x, String element, int depth) {
        OperationResult opRes = new OperationResult(x);

        if (x == null) {
            return opRes;
        }

        if (depth == element.length()) {
            if (x.isElement) {
                size--;
                x.isElement = false;
                opRes.changesWereApplied = true;
            }
        } else {
            char c = element.charAt(depth);
            OperationResult recRes = delete(x.getChild(c), element, depth + 1);
            x.setChild(c, recRes.node);
            if (recRes.changesWereApplied) {
                x.numberOfElementChildren -= 1;
            }
            opRes.changesWereApplied = recRes.changesWereApplied;
        }

        if (x.isElement) {
            return opRes;
        }

        for (char c = 'A'; c <= 'z'; c++) {
            if (x.getChild(c) != null) {
                return opRes;
            }
        }
        opRes.node = null;
        return opRes;
    }

    /**
     * Expected complexity: O(|element|)
     *
     * @return <tt>true</tt> if this set contained the specified element
     */
    @Override
    public boolean remove(String element) {
        boolean found = false;
        OperationResult opRes = delete(root, element, 0);
        root = opRes.node;
        return opRes.changesWereApplied;
    }

    /**
     * Expected complexity: O(1)
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Expected complexity: O(|prefix|)
     */
    @Override
    public int howManyStartsWithPrefix(String prefix) {
        Node x = getEndNode(root, prefix, 0);
        if (x == null) {
            return 0;
        }
        if (x.isElement) {
            return x.numberOfElementChildren + 1;
        }
        return x.numberOfElementChildren;
    }
}
