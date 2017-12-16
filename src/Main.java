public class Main {
    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);
        tree.add(6);
        System.out.println(tree.size());
    }
}
