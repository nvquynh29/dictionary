package sample;

import java.util.ArrayList;
import java.util.List;

class TrieNode {
    // Alphabet size (# of symbols)
    static final int ALPHABET_SIZE = 26;
    TrieNode[] children = new TrieNode[ALPHABET_SIZE];

    // isEndOfWord is true if the node represents
    // end of a word
    boolean isEndOfWord;

    public TrieNode(){
        isEndOfWord = false;
        for (int i = 0; i < ALPHABET_SIZE; i++)
            children[i] = null;
    }

    public boolean isEmpty() {
        if (this == null)
            return true;
        for (int i = 0; i < ALPHABET_SIZE; ++i) {
            if (this.children[i] != null) {
                return false;
            }
        }
        return true;
    }
}

public class Trie {
    static final int ALPHABET_SIZE = 26;
    private TrieNode root = new TrieNode();

    public TrieNode getRoot() {
        return root;
    }

    // If not present, inserts key into trie
    // If the key is prefix of trie node,
    // just marks leaf node
    public void insert(String key)
    {
        int level;
        int length = key.length();
        int index;

        TrieNode iterator = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level) - 'a';
            if (iterator.children[index] == null)
                iterator.children[index] = new TrieNode();

            iterator = iterator.children[index];
        }

        // mark last node as leaf
        iterator.isEndOfWord = true;
    }

    // Returns true if key presents in trie, else false
    public boolean search(String key)
    {
        int level;
        int length = key.length();
        int index;
        TrieNode iterator = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level) - 'a';

            if (iterator.children[index] == null)
                return false;

            iterator = iterator.children[index];
        }

        return (iterator != null && iterator.isEndOfWord);
    }

    public boolean isEmpty() {
        for (int i = 0; i < ALPHABET_SIZE; ++i) {
            if (root.children[i] != null) {
                return false;
            }
        }
        return true;
    }

    public int size(TrieNode node) {
        int result = 0;
        if (node.isEndOfWord) {
            result++;
        }

        for (int i = 0; i < ALPHABET_SIZE; ++i) {
            if (node.children[i] != null) {
                result += size(node.children[i]);
            }
        }
        return result;
    }


    public void display(TrieNode node, char[] str, int level) {
        if (node == null) {
            return;
        }
        if (node.isEndOfWord) {
            System.out.println(combineChars(str, level));
        }

        //Duyệt hết các nút từ a-z, ở mỗi nút đều duyệt tiếp bằng đệ quy
        for (int i = 0; i < ALPHABET_SIZE; ++i) {
            if (node.children[i] != null) {
                str[level] = (char) (i + 'a');
                display(node.children[i], str, level + 1);
            }
        }
    }

    //Remove node
    public void remove(TrieNode node, String key, int depth) {
        // If tree is empty
        if (node == null || this.isEmpty())
            return;

        // If last character of key is being processed
        if (depth == key.length()) {

            // This node is no more end of word after
            // removal of given key
            if (node.isEndOfWord) {
                node.isEndOfWord = false;
            }
            return;
        }

        // If not last character, recur for the child
        // obtained using ASCII value
        int index = key.charAt(depth) - 'a';
        remove(node.children[index], key, depth + 1);

        // If root does not have any child (its only child got
        // deleted), and it is not end of another word.
        if (node.isEmpty() && root.isEndOfWord == false) {
            node = null;
        }
    }

    public String combineChars(char[] str, int n) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < n; ++i) {
            sb.append(str[i]);
        }
        return sb.toString();
    }

    public List<String> getSortedList(TrieNode node, char[] str, int level) {
        List<String> result = new ArrayList<>();
        if (node != null) {
            if (node.isEndOfWord) {
                String word = combineChars(str, level);
                result.add(word);
            }

            for (int i = 0; i < ALPHABET_SIZE; ++i) {
                if (node.children[i] != null) {
                    str[level] = (char) (i + 'a');
                    display(node.children[i], str, level + 1);
                }
            }
        }
        return result;
    }

    //currPrefix: prefix each recursive call
    public List<String> getMatchesPrefix(TrieNode node, String currPrefix) {
        List<String> result = new ArrayList<>();
        // found a string in Trie with the given prefix
        if (node != null) {
            if (node.isEndOfWord)
            {
                result.add(currPrefix);
            }
            for (int i = 0; i < ALPHABET_SIZE; i++)
            {
                if (node.children[i] != null)
                {
                    // append current character to currPrefix string
                    char ch = (char) (i + 'a');
                    currPrefix += ch;

                    // recur over the rest
                    getMatchesPrefix(node.children[i], currPrefix);
                    // remove last character (char at node children[i])
                    currPrefix = currPrefix.substring(0, currPrefix.length());
                }
            }
        }
        return result;
    }
}
