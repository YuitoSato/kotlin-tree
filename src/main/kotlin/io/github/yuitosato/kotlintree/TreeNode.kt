package io.github.yuitosato.kotlintree

import java.util.Stack

/**
 * Class for Multi-way Trees
 */
sealed interface TreeNode<T> {
    val value: T
    val children: List<TreeNode<T>>

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each tree node by depth-first-search.
     */
    fun <S> foldNode(initial: S, operation: (acc: S, treeNode: TreeNode<T>) -> S): S

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each element by depth-first-search.
     */
    fun <S> fold(initial: S, operation: (acc: S, element: T) -> S): S

    /**
     * Returns a tree node containing the results of applying the given [transform] function
     * to each tree node in the original tree node by depth-first-search.
     */
    fun <S> mapNode(transform: (TreeNode<T>) -> S): TreeNode<S>

    /**
     * Returns a tree node containing the results of applying the given [transform] function
     * to each element in the original tree node by depth-first-search.
     */
    fun <S> map(transform: (T) -> S): TreeNode<S>

    /**
     * Performs the given [action] on each tree node.
     */
    fun <S> forEachNode(action: (TreeNode<T>) -> S)

    /**
     * Performs the given [action] on each element.
     */
    fun <S> forEach(action: (T) -> S)

    /**
     * Returns a tree node containing only tree nodes matching the given [predicate].
     */
    fun filterNode(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>?

    /**
     * Returns a tree node containing only elements matching the given [predicate].
     */
    fun filter(predicate: (T) -> Boolean): TreeNode<T>?

    /**
     * Returns tree nodes matching the given [predicate].
     */
    fun findNode(predicate: (TreeNode<T>) -> Boolean): List<TreeNode<T>>

    /**
     * Returns tree nodes matching the given [predicate].
     */
    fun find(predicate: (T) -> Boolean): List<TreeNode<T>>

    /**
     * Returns a tree node containing the results of applying the given [transform] function to each element in the original tree node and flatten the nodes.
     * See also [flatten]
     */
    fun <S> flatMapNode(prepend: Boolean, transform: (TreeNode<T>) -> TreeNode<S>): TreeNode<S>

    /**
     * Returns a tree node containing the results of applying the given [transform] function to each node in the original tree node and flatten the nodes.
     * See also [flatten]
     */
    fun <S> flatMap(prepend: Boolean, transform: (T) -> TreeNode<S>): TreeNode<S>

    /**
     * Returns a tree node at the given [indices] or `null` if the [indices] is out of bounds of this tree node.
     *
     * Returns the top of the tree node if an empty indices is received.
     * emptyList -> Returns the top of the node
     * listOf(1) -> Returns the second child node of the top of the node.
     * listOf(1, 0) -> Returns the first child node of the second child node of the top of the node.
     */
    fun getOrNull(indices: List<Int>): TreeNode<T>?

    /**
     * Returns a tree node with indices.
     */
    fun withIndices(): TreeNode<ValueWithIndices<T>>

    /**
     * Returns a tree node with zero-based levels.
     */
    fun withLevel(): TreeNode<ValueWithLevel<T>>

    /**
     * Flattens a tree node and returns a flat list of elements.
     */
    fun toFlatList(): List<T>

    /**
     * Flattens a tree node and returns a flat list of nodes.
     */
    fun toFlatListNode(): List<TreeNode<T>>

    /**
     * Returns Returns the size of nodes.
     */
    fun size(): Int

    /**
     * Returns the formatted string of a node
     */
    fun toFormattedString(): String

    companion object {

        fun <T> of(value: T, children: List<TreeNode<T>>): TreeNode<T> =
            MutableTreeNode.of(value, children.map { it.asMutable() }.toMutableList())

        fun <T> of(value: T): MutableTreeNode<T> = MutableTreeNode.of(value)
    }
}

/**
 * Flatten a nested tree node.
 * If [prepend] is true, child nodes in each element are prepended to each node.
 */
fun <T> TreeNode<TreeNode<T>>.flatten(prepend: Boolean): TreeNode<T> {
    if (this is MutableTreeNode) {
        return (this.map { it.asMutable() }.asMutable()).flatten(prepend)
    } else {
        throw Exception("TODO")
    }
}

internal fun <T> TreeNode<T>.asMutable(): MutableTreeNode<T> =
    this as MutableTreeNode<T>

/**
 * Makes a deep copy of a node and returns it as MutableTreeNode.
 */
fun <T> TreeNode<T>.toMutable(): MutableTreeNode<T> {
    return this.asMutable().foldNodeInternal(MutableTreeNode.of(this.value)) { acc, treeNode, indices ->
        val level = indices.size
        if (level == 0) {
            acc
        } else {
            val newTreeNode = MutableTreeNode.of(treeNode.value)
            acc.getOrNull(indices.take(indices.size - 1))
                ?.children?.add(newTreeNode)
            acc
        }
    }
}

/**
 * Mutable TreeNode
 */
class MutableTreeNode<T> private constructor(
    override val value: T,
    override val children: MutableList<MutableTreeNode<T>>
) : TreeNode<T> {

    override fun <S> foldNode(initial: S, operation: (acc: S, treeNode: TreeNode<T>) -> S): S =
        foldNodeInternal(initial) { acc, treeNode, _ -> operation(acc, treeNode) }

    override fun <S> fold(initial: S, operation: (acc: S, element: T) -> S): S =
        foldNode(initial) { acc, treeNode -> operation(acc, treeNode.value) }

    override fun <S> mapNode(transform: (TreeNode<T>) -> S): MutableTreeNode<S> {
        val initial = of(transform(this))
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val level = indices.size
            if (level == 0) {
                acc
            } else {
                val newTreeNode = of(transform(treeNode))
                acc.getOrNull(indices.take(indices.size - 1))
                    ?.children?.add(newTreeNode)
                acc
            }
        }
    }

    override fun <S> map(transform: (T) -> S): MutableTreeNode<S> = mapNode { treeNode -> transform(treeNode.value) }

    override fun <S> forEachNode(action: (TreeNode<T>) -> S) {
        mapNode(action)
    }

    override fun <S> forEach(action: (T) -> S) = forEachNode { treeNode -> action(treeNode.value) }

    override fun filterNode(predicate: (TreeNode<T>) -> Boolean): MutableTreeNode<T>? {
        val initial: MutableTreeNode<T>? = null
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val condition = predicate(treeNode)
            val newTreeNode = of(treeNode.value)
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

    override fun filter(predicate: (T) -> Boolean): MutableTreeNode<T>? =
        filterNode { treeNode -> predicate(treeNode.value) }

    override fun findNode(predicate: (TreeNode<T>) -> Boolean): List<MutableTreeNode<T>> {
        val initial: List<MutableTreeNode<T>> = listOf()
        return foldNodeInternal(initial) { acc, treeNode, _ ->
            if (predicate(treeNode)) {
                acc.plus(treeNode)
            } else {
                acc
            }
        }
    }

    override fun find(predicate: (T) -> Boolean): List<MutableTreeNode<T>> =
        findNode { treeNode -> predicate(treeNode.value) }

    override fun <S> flatMapNode(prepend: Boolean, transform: (TreeNode<T>) -> TreeNode<S>): MutableTreeNode<S> =
        mapNode(transform).flatten(prepend).asMutable()

    override fun <S> flatMap(prepend: Boolean, transform: (T) -> TreeNode<S>): MutableTreeNode<S> =
        flatMapNode(prepend) { treeNode -> transform(treeNode.value) }

    override fun getOrNull(indices: List<Int>): MutableTreeNode<T>? {
        var current = this
        indices.forEach { index ->
            val currentOpt = current.children.getOrNull(index) ?: return null
            current = currentOpt
        }
        return current
    }

    override fun withIndices(): MutableTreeNode<ValueWithIndices<T>> {
        val initial = of(ValueWithIndices(listOf(), value))
        return foldNodeInternal(initial) { acc, treeNode, indices ->
            val level = indices.size
            if (level == 0) {
                acc
            } else {
                val newTreeNode = of(ValueWithIndices(indices, treeNode.value))
                acc.getOrNull(indices.take(indices.size - 1))
                    ?.children?.add(newTreeNode)
                acc
            }
        }
    }

    override fun withLevel(): MutableTreeNode<ValueWithLevel<T>> =
        withIndices().map { ValueWithLevel(it.indices.size, it.value) }

    override fun toFlatList(): List<T> = fold(emptyList()) { acc, element -> acc.plus(element) }

    override fun toFlatListNode(): List<MutableTreeNode<T>> =
        foldNode(emptyList()) { acc, node -> acc.plus(node.asMutable()) }

    override fun toFormattedString(): String {
        val levelToEnd = mutableMapOf<Int, Boolean>()
        return this.foldNodeInternal(emptyList<String>()) { acc, treeNode, indices ->
            val level = indices.size

            if (level == 0) {
                levelToEnd[level] = true
                acc.plus(treeNode.value.toString())
            } else {
                val nextNode = this.getOrNull(indices.take(indices.size - 1).plus(indices.last() + 1))
                val skipDividerOffset = levelToEnd.keys.size - 1
                val prefixDivider = (0 until level - 1 - skipDividerOffset).joinToString("") { "│   " }
                val prefixSpace = (0 until skipDividerOffset).joinToString("") { "    " }
                val prefix = prefixSpace + prefixDivider

                if (nextNode != null) {
                    acc.plus(prefix + "├── " + treeNode.value.toString())
                } else {
                    val previousLevelEnd = levelToEnd[level - 1] ?: false
                    if (previousLevelEnd) {
                        levelToEnd[level] = true
                    }
                    acc.plus(prefix + "└── " + treeNode.value.toString())
                }
            }
        }.joinToString("\n")
    }

    override fun size(): Int = foldNodeInternal(0) { acc, _, _ -> acc + 1 }

    fun addChildNode(node: MutableTreeNode<T>) {
        children.add(node)
    }

    /**
     * Accumulates value starting with [initial] value and applying [operation] to current accumulator value and each tree node by depth-first-search.
     *
     * The "indices" argument of the [operation] is the location of the node currently being processed.
     */
    internal fun <S> foldNodeInternal(
        initial: S,
        operation: (acc: S, treeNode: MutableTreeNode<T>, indices: List<Int>) -> S
    ): S {
        val nodeAndIndicesStack: Stack<Pair<MutableTreeNode<T>, List<Int>>> = Stack()
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

        fun <T> of(value: T, children: MutableList<MutableTreeNode<T>>): MutableTreeNode<T> =
            MutableTreeNode(value, children)

        fun <T> of(value: T): MutableTreeNode<T> = MutableTreeNode(value, mutableListOf())
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
 * Flatten a nested tree node.
 * If [prepend] is true, child nodes in each element are prepended to each node.
 */
fun <T> MutableTreeNode<MutableTreeNode<T>>.flatten(prepend: Boolean): TreeNode<T> {
    val nodeAndIndicesStack: Stack<Pair<MutableTreeNode<MutableTreeNode<T>>, List<Int>>> = Stack()
    val resultTree = this.value
    this.children.withIndex().reversed()
        .forEach { (index, childTreeNode) -> nodeAndIndicesStack += childTreeNode to emptyList<Int>().plus(index + if (prepend) resultTree.children.size else 0) }

    while (nodeAndIndicesStack.isNotEmpty()) {
        val (treeNode, indices) = nodeAndIndicesStack.pop()
        val newTreeNode = treeNode.value
        resultTree.getOrNull(indices.take(indices.size - 1))?.children?.add(indices.last(), newTreeNode)
        treeNode.children.withIndex().reversed()
            .forEach { (index, childTreeNode) -> nodeAndIndicesStack += childTreeNode to indices.plus(index + if (prepend) newTreeNode.children.size else 0) }
    }

    return resultTree
}

/**
 * Value with indices for TreeNode operations.
 */
data class ValueWithIndices<T>(val indices: List<Int>, val value: T)

/**
 * Value with levels for TreeNode operations.
 */
data class ValueWithLevel<T>(val level: Int, val value: T)

/**
 * Returns a tree node. same as Tree.of.
 */
fun <T> nodeOf(value: T, children: List<TreeNode<T>>): TreeNode<T> = TreeNode.of(value, children)

/**
 * Returns a leaf that does not have any children
 */
fun <T> leafOf(value: T): TreeNode<T> = TreeNode.of(value)
