package huffman;

/**
 * @author DanielDFY, Dev1Art
 * @project HuffCompressor
 */
public class Node implements Comparable<Node> {
    protected final char ch;
    protected final int freq;
    protected final Node left;
    protected final Node right;

    Node(char ch, int freq, Node left, Node right) {
        this.ch    = ch;
        this.freq  = freq;
        this.left  = left;
        this.right = right;
    }

    protected boolean isLeaf() {
        // check for a correct trie's leafs linking
        boolean inv_trie_state = !(((left == null) && (right == null)) || ((left != null) && (right != null)));
        if(inv_trie_state) throw new RuntimeException("Invalid tree state");

        return (left == null);
    }

    @Override
    public int compareTo(Node that) {
        return this.freq - that.freq;
    }
}
