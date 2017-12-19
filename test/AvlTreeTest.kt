import org.junit.Assert
import org.junit.Test
import java.util.*

class AvlTreeTest {
    @Test
    fun testRemove() {
        val random = Random()
        for (iteration in 1..1000) {
            println("----------$iteration----------")
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val treeSet = TreeSet<Int>()
            val binarySet = AVLTree<Int>()
            for (element in list) {
                treeSet += element
                binarySet += element
            }
            val toRemove = list[random.nextInt(list.size)]
            println("Removing $toRemove from ${list.sorted()}")
            treeSet.remove(toRemove)
            binarySet.remove(toRemove)
            println(treeSet)
            println("[${binarySet.joinToString(separator = ", ")}]")
            Assert.assertEquals("After removal of $toRemove from $list", treeSet, binarySet)
            Assert.assertEquals(treeSet.size, binarySet.size)
            for (element in list) {
                val inn = element != toRemove
                Assert.assertEquals("$element should be ${if (inn) "in" else "not in"} tree", inn, element in binarySet)
            }
        }
    }
}