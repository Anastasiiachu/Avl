import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class AVLTree<T extends Comparable<T>> implements SortedSet<T> {
    public Comparator<? super T> comparator;
    private Node root;
    private int size = 0;
    private class Node {
        public T key;
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
            //Считаем рразницу высот относительно правого поддрева
            int leftHeight = (left != null) ? left.getHeight() : 0;
            int rightHeight = (right != null) ? right.getHeight() : 0;
            return rightHeight - leftHeight;
        }
        //Каждый метод поворота должен ваозвращать Node, так как они меняют корень дерева
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
            //Нам нужно проветить, нет ли дибаланса в текущей вершине
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
            int comparison = key.compareTo(this.key);
            if (comparison == -1) {
                if (this.left != null)
                    this.left = this.left.insertKey(key);
                else {
                    this.left = new Node(key);
                    size++;
                }
            } else if (comparison == 1) {
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
            if (this.left == null) return this; else return this.left.min();
        }
        //Находит и удаляет минимальный элемент в дереве this, с учетом свойств АВЛ дерева
        //Так как метод изменяет структуру дерева - снова проводим балансировку и возвращаем новую вершину
        private Node removeMin() {
            if (this.left == null)
                return this.right;
            this.left = left.removeMin();
            return this.balanceTree();
        }
        public Node remove(T key) {
            int comparison = key.compareTo(this.key);
            if (comparison == -1 && this.left != null){
                this.left = this.left.remove(key);
            } else if (comparison == 1 && this.right != null) {
                this.right = this.right.remove(key);
            } else if (comparison == 0) {
                Node left = this.left;
                Node right = this.right;
                if (right == null) return left;
                //Если правое поддрево все же существует
                Node min = right.min();
                min.right = right.removeMin();
                min.left = left;
                size--;
                return min.balanceTree();
            }
            return null;
        }
        public boolean contains(T key) {
            int comparison = key.compareTo(this.key);
            if (comparison == -1 && this.left != null) {
                return this.left.contains(key);
            } else if (comparison == 1 && this.right != null) {
                return this.right.contains(key);
            } else return comparison == 0;
        }
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return null;
    }

    @Override
    public T first() {
        return root.min().key;
    }

    @Override
    public T last() {
        return null;
    }

    @Override
    public int size() {
        System.out.println(root.getHeight());
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        try {
            return (root != null && root.contains((T)o));
        } catch (ClassCastException e) {
            return false;
        }

    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        if (!this.contains(t)) {
            if (root == null) {
                root = new Node(t);
                size++;
            }
            else root = root.insertKey(t);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            T t = (T) o;
            if (this.contains(t)) {
                root = root.remove(t);
                return true;
            } else {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        root = null;
    }
}
