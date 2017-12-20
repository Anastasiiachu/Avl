import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class AVLTree<T extends Comparable<T>> implements SortedSet<T> {
    private Comparator<? super T> comparator;

    private Node root;
    private int size = 0;

    private class Node {
        private T key;
        int height;
        Node left;
        Node right;

        Node (T key){
            this.key = key;
            height = 1;
        }

        private int getHeight() {
            return height;
        }
        private void countHeight() {
            int leftHeight = (left != null) ? left.getHeight() : 0;
            int rightHeight = (right != null) ? right.getHeight() : 0;
            this.height = ((leftHeight > rightHeight) ? leftHeight : rightHeight) + 1;
        }
        private int balanceFactor() {
            //Считаем разницу высот относительно правого поддрева
            int leftHeight = (left != null) ? left.getHeight() : 0;
            int rightHeight = (right != null) ? right.getHeight() : 0;
            return rightHeight - leftHeight;
        }
        //Каждый метод поворота должен возвращать Node, так как они меняют корень дерева
        private Node turnLeft() {
            //Сохраняем ссылку, чтобы не потерять ее
            Node p = this.right;
            this.right = p.left;
            p.left = this;
            this.countHeight();
            p.countHeight();
            return p;
        }
        private Node turnRight() {
            Node q = this.left;
            this.left = q.right;
            q.right = this;
            this.countHeight();
            q.countHeight();
            return q;
        }
        private Node balanceTree() {
            this.countHeight();
            //Нам нужно проветить, нет ли дисбаланса в текущей вершине
            if (this.balanceFactor() == 2) {
                //Если дисбаланс справа
                if (this.right != null && this.right.balanceFactor() < 0) {
                    this.right = this.right.turnRight();
                }
                return this.turnLeft();
            } else if (this.balanceFactor() == -2) {
                //Если дисбаланс слева
                if (this.left != null && this.left.balanceFactor() > 0) {
                    this.left = this.left.turnLeft();
                }
                return this.turnRight();
            }
            return this;
        }
        private Node insertKey(T key) {
            int comparison = compare(key, this.key);
            if (comparison < 0) {
                if (this.left != null)
                    this.left = this.left.insertKey(key);
                else {
                    this.left = new Node(key);
                    size++;
                }
            } else if (comparison > 0) {
                if (this.right != null)
                    this.right = this.right.insertKey(key);
                else {
                    this.right = new Node(key);
                    size++;
                }
            }
            return this.balanceTree();
        }
        //Находит минимальный элемент в дереве this
        private Node min() {
            if (this.left == null) return this;
            else return this.left.min();
        }
        private Node max() {
            if (this.right == null) return this;
            else return this.right.max();
        }

        private Node next(T key, Node prev) {
            int comparison  = key.compareTo(this.key);
            Node min = (comparison < 0 && this.key.compareTo(prev.key) < 0) ? this : prev;
            if (comparison < 0 && this.left != null) {
                min = this.left.next(key, min);
            } else if (comparison > 0 && this.right != null) {
                min = this.right.next(key, min);
            } else if (comparison == 0) {
                if (this.right != null) {
                    Node minRight = this.right.min();
                    min = (minRight.key.compareTo(min.key) < 0) ? minRight : prev;
                }
            }
            return min;
        }
        //Находит и удаляет минимальный элемент в дереве this, с учетом свойств АВЛ дерева
        //Так как метод изменяет структуру дерева - снова проводим балансировку и возвращаем новую вершину
        private Node removeMin() {
            if (this.left == null)
                return this.right;
            this.left = left.removeMin();
            return this.balanceTree();
        }
        private Node remove(T key) {
            int comparison = key.compareTo(this.key);
            if (comparison < 0 && this.left != null){
                this.left = this.left.remove(key);
            } else if (comparison > 0 && this.right != null) {
                this.right = this.right.remove(key);
            } else if (comparison == 0) {
                size--;
                Node left = this.left;
                Node right = this.right;
                if (right == null) return left;
                //Если правое поддрево все же существует
                Node min = right.min();
                min.right = right.removeMin();
                min.left = left;
                return min.balanceTree();
            }
            return this;
        }
        private boolean contains(T key) {
            int comparison = key.compareTo(this.key);
            if (comparison < 0 && this.left != null) {
                return this.left.contains(key);
            } else if (comparison > 0 && this.right != null) {
                return this.right.contains(key);
            } else return comparison == 0;
        }
    }
    private class AvlIterator implements Iterator<T> {
        Node first = null;
        Node current = null;
        Node last = null;
        boolean lastReached = false;

        private AvlIterator() {
            first = root.min();
            last = root.max();
        }

        @Override
        public boolean hasNext() {
            return (current != null) ? (!lastReached) : root != null;
        }
        @Override
        public T next() {
            Node next = (current == null) ? first : root.next(current.key, last);
            if (lastReached) throw new NoSuchElementException();
            lastReached = (next == last);
            current = next;
            return next.key;
        }

    }
    private int compare(T o1, T o2) {
        return (comparator != null) ? comparator.compare(o1, o2) : o1.compareTo(o2);
    }

    public AVLTree(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
    public AVLTree(){}

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (!root.contains(fromElement) || !root.contains(toElement))
            throw new NoSuchElementException();
        AVLTree<T> subSet = new AVLTree<>();
        Iterator<T> iterator = new AvlIterator();
        while (iterator.hasNext()) {
            T curr = iterator.next();
            if (compare(curr, fromElement) >= 0 && compare(curr, toElement) <= 0)
                subSet.add(curr);
        }
        return subSet;
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        return subSet(first(), toElement);
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return subSet(fromElement, last());
    }

    @Override
    public T first() {
        return root.min().key;
    }

    @Override
    public T last() {
        return root.max().key;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        return root != null && root.contains(t);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new AvlIterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        Iterator<T> iterator = new AvlIterator();
        Object[] o = new Object[size];
        for (int i = 0; i < size; i++) {
            o[i] = iterator.next();
        }
        return o;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(@NotNull T1[] a) {
        if (a.length < size)
            a = (T1[])java.lang.reflect.Array.newInstance(a.getClass(), size);
        Iterator<T> iterator = new AvlIterator();
        Object[] result = a;
        for (int i = 0; i < size; i++) {
            result[i] = iterator.next();
        }
        if (a.length > size)
            for (int i = size; i < a.length; i++)
                result[i] = null;
        return a;
    }

    @Override
    public boolean add(T t) {
        if (root == null) {
                root = new Node(t);
                size++;
            }
            else root = root.insertKey(t);
            return true;
    }

    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
            T t = (T) o;
                root = root.remove(t);
                return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        Object[] elements = c.toArray();
        for (Object o : elements) {
            @SuppressWarnings("unchecked")
                    T t = (T) o;
            if (!root.contains(t)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        c.forEach(E -> root.insertKey(E));
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Node newRoot = null;
        Object[] elements = c.toArray();
        for (Object o : elements) {
            @SuppressWarnings("unchecked")
                    T t = (T) o;
            if (root.contains(t)) {
                if (newRoot == null) newRoot = new Node(t);
                else newRoot.insertKey(t);
            }
        }
        root = newRoot;
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        c.forEach(E -> {
            @SuppressWarnings("unchecked")
            T t = (T) E;
            root.remove(t);
        });
        return true;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }
}
