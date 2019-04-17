import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.BiConsumer;

/**
 * A simple implementation of binary search trees.
 */
public class SimpleBST<K, V> implements SimpleMap<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The root of our tree. Initialized to null for an empty tree.
   */
  BSTNode<K, V> root;

  /**
   * The comparator used to determine the ordering in the tree.
   */
  Comparator<K> comparator;

  /**
   * The size of the tree.
   */
  int size;

  /**
   * A cached value (useful in some circumstances.
   */
  V cachedValue;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new binary search tree that orders values using the specified comparator.
   */
  public SimpleBST(Comparator<K> comparator) {
    this.comparator = comparator;
    this.root = null;
    this.size = 0;
    this.cachedValue = null;
  } // SimpleBST(Comparator<K>)

  /**
   * Create a new binary search tree that orders values using a not-very-clever default comparator.
   */
  public SimpleBST() {
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SimpleBST()


  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  @Override
  public V set(K key, V value) {
    if (key == null) {
      throw new NullPointerException("Cannot set a value to a null key.");
    } // if you try using a null key
    root = set(key, value, root);
    return cachedValue;
  } // set(K,V)

  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    return get(key, root);
  } // get(K,V)

  @Override
  public int size() {
    // TODO Auto-generated method stub
    return 0;
  } // size()

  @Override
  public boolean containsKey(K key) {
    // TODO Auto-generated method stub
    return false;
  } // containsKey(K)

  @Override
  public V remove(K key) {
    // TODO Auto-generated method stub
    return null;
  } // remove(K)

  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    if (root == null) {
      throw new NullPointerException("Cannot apply a function to no pairs.");
    } // if there are no pairs
    root = forEach(action, root);
  } // forEach

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   */
  public void dump(PrintWriter pen) {
    dump(pen, root, "");
  } // dump(PrintWriter)


  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Dump a portion of the tree to some output location.
   */
  void dump(PrintWriter pen, BSTNode<K, V> node, String indent) {
    if (node == null) {
      pen.println(indent + "<>");
    } else {
      pen.println(indent + node.key + ": " + node.value);
      if ((node.left != null) || (node.right != null)) {
        dump(pen, node.left, indent + "  ");
        dump(pen, node.right, indent + "  ");
      } // if has children
    } // else
  } // dump

  /**
   * Apply a function to each key/value pair.
   */
  BSTNode<K, V> forEach(BiConsumer<? super K, ? super V> action, BSTNode<K, V> node) {
    if (node == null) {
      return node;
    } // if we reached the bottom of the tree
    node.left = forEach(action, node.left);
    action.accept(node.key, node.value);
    node.right = forEach(action, node.right);
    return node;
  } // forEach(BiConsumer<? super K, ? super V>, BSTNode<K, V>)

  /**
   * Set the value associated with key in a modified subtree that is returned.
   */
  BSTNode<K, V> set(K key, V value, BSTNode<K, V> node) {
    if (node == null) {
      cachedValue = null;
      BSTNode<K, V> newNode = new BSTNode<K, V>(key, value);
      size++;
      return newNode;
    } // if the node is null
    int comp = comparator.compare(key, node.key);
    // don't forget this might have to be else if
    if (comp == 0) {
      cachedValue = node.value;
      node.value = value;
      return node;
    } // if we found the right key
    else if (comp < 0) {
      node.left = set(key, value, node.left);
      return node;
    } // else if we're on a larger key
    else {
      node.right = set(key, value, node.right);
      return node;
    } // else we're on a smaller key
  } // set(K, V, BSTNode<K, V>)

  /**
   * Get the value associated with a key in a subtree rooted at node. See the top-level get for more
   * details.
   */
  V get(K key, BSTNode<K, V> node) {
    if (node == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } // if we got to the bottom without finding it
    int comp = comparator.compare(key, node.key);
    if (comp == 0) {
      return node.value;
    } // if we found the right key
    else if (comp < 0) {
      return get(key, node.left);
    } // else if we're on a larger key
    else {
      return get(key, node.right);
    } // else we're on a smaller key
  } // get(K, BSTNode<K,V>)

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the other iterators.)
   */
  Iterator<BSTNode<K, V>> nodes() {
    return new Iterator<BSTNode<K, V>>() {

      Stack<BSTNode<K, V>> stack = new Stack<BSTNode<K, V>>();
      boolean initialized = false;

      @Override
      public boolean hasNext() {
        checkInit();
        return !stack.empty();
      } // hasNext()

      @Override
      public BSTNode<K, V> next() {
        checkInit();
        // TODO Auto-generated method stub
        return null;
      } // next();

      void checkInit() {
        if (!initialized) {
          stack.push(SimpleBST.this.root);
          initialized = true;
        } // if
      } // checkInit
    }; // new Iterator
  } // nodes()

} // class SimpleBST
