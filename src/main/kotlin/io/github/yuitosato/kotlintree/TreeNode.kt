package io.github.yuitosato.kotlintree

import java.util.Stack

/**
 * Class for Multi-way Trees
 */
class TreeNode<T> private constructor(
    val value: T,
    val children: MutableList<TreeNode<T>>
) {

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each tree node by depth-first-search.
     */
    fun <S> foldNode(initial: S, operation: (acc: S, treeNode: TreeNode<T>) -> S): S =
        foldNodeInternal(initial) { acc, treeNode, indices -> operation(acc, treeNode) }

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each element by depth-first-search.
     */
    fun <S> fold(initial: S, operation: (acc: S, element: T) -> S): S =
        foldNode(initial) { acc, treeNode -> operation(acc, treeNode.value) }

    /**
     * Returns a tree node containing the results of applying the given [transform] function
     * to each tree node in the original tree node by depth-first-search.
     */
    fun <S> mapNode(transform: (TreeNode<T>) -> S): TreeNode<S> {
        val initial: TreeNode<S> = leafOf(transform(this))
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val level = indices.size
            if (level == 0) acc else {
                val newTreeNode = leafOf(transform(treeNode))
                acc.getOrNull(indices.take(indices.size - 1))
                    ?.children?.add(newTreeNode)
                acc
            }
        }
    }

    /**
     * Returns a tree node containing the results of applying the given [transform] function
     * to each element in the original tree node by depth-first-search.
     */
    fun <S> map(transform: (T) -> S): TreeNode<S> = mapNode { treeNode -> transform(treeNode.value) }

    /**
     * Performs the given [action] on each tree node.
     */
    fun <S> forEachNode(action: (TreeNode<T>) -> S) {
        mapNode(action)
    }

    /**
     * Performs the given [action] on each element.
     */
    fun <S> forEach(action: (T) -> S) = forEachNode { treeNode -> action(treeNode.value) }

    /**
     * Returns a tree node containing only tree nodes matching the given [predicate].
     */
    fun filterNode(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>? {
        val initial: TreeNode<T>? = null
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val condition = predicate(treeNode)
            val newTreeNode = leafOf(treeNode.value)
            when {
                !condition -> acc
                acc == null -> newTreeNode
                else -> {
                    acc.getOrNull(indices.take(indices.size - 1))
                        ?.children?.add(newTreeNode)
                    acc
                }
            }
        }
    }

    /**
     * Returns a tree node containing only elements matching the given [predicate].
     */
    fun filter(predicate: (T) -> Boolean): TreeNode<T>? =
        filterNode { treeNode -> predicate(treeNode.value) }

    /**
     * Returns tree nodes matching the given [predicate].
     */
    fun findNode(predicate: (TreeNode<T>) -> Boolean): List<TreeNode<T>> {
        val initial: List<TreeNode<T>> = listOf()
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            if (predicate(treeNode)) {
                acc.plus(treeNode)
            } else acc
        }
    }

    /**
     * Returns tree nodes matching the given [predicate].
     */
    fun find(predicate: (T) -> Boolean): List<TreeNode<T>> = findNode { treeNode -> predicate(treeNode.value) }

    /**
     * Returns a tree node at the given [indices] or `null` if the [indices] is out of bounds of this tree node.
     *
     * Returns the top of the tree node if an empty indices is received.
     * emptyList -> Returns the top of the node
     * listOf(1) -> Returns the second child node of the top of the node.
     * listOf(1, 0) -> Returns the first child node of the second child node of the top of the node.
     */
    fun getOrNull(indices: List<Int>): TreeNode<T>? {
        var current = this
        indices.forEach { index ->
            val currentOpt = current.children.getOrNull(index) ?: return null
            current = currentOpt
        }
        return current
    }

    /**
     * Returns a tree node with indices.
     */
    fun withIndices(): TreeNode<IndexedValue<T>> {
        val initial = leafOf(IndexedValue(listOf(), value))
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val level = indices.size
            if (level == 0) acc else {
                val newTreeNode = leafOf(IndexedValue(indices, treeNode.value))
                acc.getOrNull(indices.take(indices.size - 1))
                    ?.children?.add(newTreeNode)
                acc
            }
        }
    }

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each tree node by depth-first-search.
     *
     * The "indices" argument of the [operation] is the location of the node currently being processed.
     */
    internal fun <S> foldNodeInternal(
        initial: S,
        operation: (acc: S, treeNode: TreeNode<T>, indices: List<Int>) -> S
    ): S {
        val nodeAndIndicesStack: Stack<Pair<TreeNode<T>, List<Int>>> = Stack()
        nodeAndIndicesStack += this to emptyList()
        var acc = initial

        while (nodeAndIndicesStack.isNotEmpty()) {
            val (treeNode, indices) = nodeAndIndicesStack.pop()
            acc = operation(acc, treeNode, indices)
            treeNode.children.withIndex().reversed()
                .forEach { (index, childTreeNode) -> nodeAndIndicesStack += childTreeNode to indices.plus(index) }
        }

        return acc
    }

    companion object {

        fun <T> of(value: T, children: MutableList<TreeNode<T>>): TreeNode<T> = TreeNode(value, children)
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

/**
 * Value with indices for TreeNode operations.
 */
data class IndexedValue<T>(val indices: List<Int>, val value: T)

/**
 * Returns a tree node. same as Tree.of.
 */
fun <T> nodeOf(value: T, children: MutableList<TreeNode<T>>): TreeNode<T> = TreeNode.of(value, children)

/**
 * Returns a leaf that does not have any children
 */
fun <T> leafOf(value: T): TreeNode<T> = TreeNode.of(value, mutableListOf())