package huffman;

import io.InStream;
import io.OutStream;
import java.util.PriorityQueue;

/**
 * @author DanielDFY, Dev1Art
 * @project HuffCompressor
 */
public class Huffman {

    // alphabet size of extended ASCII
    private static final int R = 256;

    // can't be instancing
    private Huffman() {}

    public static void compress(InStream binaryIn, OutStream binaryOut) {
        String data = binaryIn.readString();
        char[] input = data.toCharArray();

        // tabulate frequency counts
        int[] freq = new int[R];
        for (char c : input) ++freq[c];

        // build Huffman trie
        Node root = buildTrie(freq);

        // build code table
        String[] st = new String[R];
        buildCode(st, root, "");

        // write trie for decoder
        writeTrie(binaryOut, root);

        // write number of bytes of the original uncompressed data
        binaryOut.write(input.length);

        // use Huffman code to encode input
        for (char c : input) {
            String code = st[c];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    binaryOut.write(false);
                } else if (code.charAt(j) == '1') {
                    binaryOut.write(true);
                } else throw new IllegalStateException("Illegal state");
            }
        }
    }

    private static Node buildTrie(int[] freq) {
        // initialization
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (char i = 0; i < R; ++i) {
            if (freq[i] > 0)
                pq.add(new Node(i, freq[i], null, null));
        }

        // in case there is only one character with a nonzero frequency
        if (pq.size() == 1) {
            if (freq['\0'] == 0)
                pq.add(new Node('\0', 0, null, null));
            else
                pq.add(new Node('\1', 0, null, null));
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left = pq.remove();
            Node right = pq.remove();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }

        return pq.remove();
    }

    // make a look-up table from symbols and their encodings
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left, s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            st[x.ch] = s;
        }
    }

    // write trie to a file
    private static void writeTrie(OutStream binaryOut, Node x) {
        if (x.isLeaf()) {
            binaryOut.write(true);
            binaryOut.write(x.ch, 8);
            return;
        }
        binaryOut.write(false);

        writeTrie(binaryOut, x.left);
        writeTrie(binaryOut, x.right);
    }

    public static void decompress(InStream binaryIn, OutStream binaryOut) {
        // read in Huffman trie from input stream
        Node root = readTrie(binaryIn);

        // number of bytes to write
        int length = binaryIn.readInt();

        // decompress using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = binaryIn.readBoolean();
                if (bit) x = x.right;
                else x = x.left;
            }
            binaryOut.write(x.ch, 8);
        }
    }

    // read trie from input stream
    private static Node readTrie(InStream in) {
        boolean isLeaf = in.readBoolean();
        if (isLeaf) {
            return new Node(in.readChar(), -1, null, null);
        } else {
            return new Node('\0', -1, readTrie(in), readTrie(in));
        }
    }
}
