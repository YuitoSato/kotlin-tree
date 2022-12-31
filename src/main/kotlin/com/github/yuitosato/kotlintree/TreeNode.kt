package com.github.yuitosato.kotlintree

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
     *
     * The "currentIndices" argument of the [operation] is the location of the node currently being processed.
     */
    fun <S> foldNode(initial: S, operation: (acc: S, treeNode: TreeNode<T>, currentIndices: List<Int>) -> S): S {
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

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each element by depth-first-search.
     *
     * The "currentIndices" argument of the [operation] is the location of the node currently being processed.
     */
    fun <S> fold(initial: S, operation: (acc: S, element: T, currentIndices: List<Int>) -> S): S {
        return foldNode(initial) { acc, treeNode, currentIndices -> operation(acc, treeNode.value, currentIndices) }
    }

    /**
     * Returns a tree node containing the results of applying the given [transform] function
     * to each tree node in the original tree node by depth-first-search.
     */
    fun <S> mapNode(transform: (TreeNode<T>) -> S): TreeNode<S> {
        val initial: TreeNode<S> = leafOf(transform(this))
        return foldNode(initial) { acc, treeNode, currentIndices ->
            val level = currentIndices.size
            if (level == 0) acc else {
                val newTreeNode = leafOf(transform(treeNode))
                acc.getOrNull(currentIndices.take(currentIndices.size - 1))
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
    fun filterByNodeCondition(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>? {
        val initial: TreeNode<T>? = null
        return foldNode(initial) { acc, treeNode, currentIndices ->
            val condition = predicate(treeNode)
            val newTreeNode = leafOf(treeNode.value)
            when {
                !condition -> acc
                acc == null -> newTreeNode
                else -> {
                    acc.getOrNull(currentIndices.take(currentIndices.size - 1))
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
        filterByNodeCondition { treeNode -> predicate(treeNode.value) }

    /**
     * Returns a tree node containing only tree nodes matching the given [predicate].
     */
    fun findByNodeCondition(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>? {
        var resultTreeNode: TreeNode<T>? = null
        this.forEachNode { treeNode ->
            if (predicate(treeNode) && resultTreeNode == null) {
                resultTreeNode = treeNode
            }
        }
        return resultTreeNode
    }

    /**
     * Returns a tree node containing only elements matching the given [predicate].
     */
    fun find(predicate: (T) -> Boolean): TreeNode<T>? = findByNodeCondition { treeNode -> predicate(treeNode.value) }

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
 * Returns a tree node. same as Tree.of.
 */
fun <T> nodeOf(value: T, children: MutableList<TreeNode<T>>): TreeNode<T> = TreeNode.of(value, children)

/**
 * Returns a leaf that does not have any children
 */
fun <T> leafOf(value: T): TreeNode<T> = TreeNode.of(value, mutableListOf())
