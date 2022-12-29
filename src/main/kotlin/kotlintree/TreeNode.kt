package kotlintree

import java.util.*

class TreeNode<T>(
    val value: T,
    val children: MutableList<TreeNode<T>>
) {
    private data class FoldItem<T>(
        val treeNode: TreeNode<T>,
        val indexes: List<Int>,
    )

    fun <S> foldTree(initial: S, f: (acc: S, treeNode: TreeNode<T>, currentIndexes: List<Int>) -> S): S {
        val queue: Queue<FoldItem<T>> = LinkedList()
        queue += FoldItem(this, emptyList())
        var acc = initial

        while (queue.isNotEmpty()) {
            val (treeNode, indexes) = queue.poll()
            acc = f(acc, treeNode, indexes)
            treeNode.children.withIndex().forEach { (index, childTreeNode) -> queue += FoldItem(childTreeNode, indexes.plus(index)) }
        }

        return acc
    }

    fun <S> fold(initial: S, f: (acc: S, element: T, currentIndexes: List<Int>) -> S): S {
        return foldTree(initial) { acc, treeNode, currentIndexes -> f(acc, treeNode.value, currentIndexes) }
    }

    fun <S> map(f: (T) -> S): TreeNode<S> {
        val initial: TreeNode<S> = TreeNode(f(value), mutableListOf())
        return fold(initial) { acc, element, currentIndexes ->
            val level = currentIndexes.size
            if (level == 0) acc else {
                val newTree = TreeNode(f(element), mutableListOf())
                acc.findSubTreeByIndexes(currentIndexes.take(currentIndexes.size - 1))
                    ?.children?.add(newTree)
                acc
            }
        }
    }

    fun filter(predicate: (T) -> Boolean): TreeNode<T>? {
        val initial: TreeNode<T>? = null
        return fold(initial) { acc, element, currentIndexes ->
            val condition = predicate(element)
            val newTree = TreeNode(element, mutableListOf())
            when {
                !condition -> acc
                acc == null -> newTree
                else -> {
                    acc.findSubTreeByIndexes(currentIndexes.take(currentIndexes.size - 1))
                        ?.children?.add(newTree)
                    acc
                }
            }
        }
    }

    fun findSubTreeByIndexes(indexes: List<Int>): TreeNode<T>? {
        var current = this
        indexes.forEach { index ->
            val currentOpt = current.children.getOrNull(index)
            if (currentOpt == null) return currentOpt
            current = currentOpt
        }
        return current
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeNode<*>

        if (value != other.value) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + children.hashCode()
        return result
    }

    override fun toString(): String {
        return "TreeNode($value, $children)"
    }
}
