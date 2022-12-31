package com.github.yuitosato.kotlintree

import java.util.Stack

class TreeNode<T> private constructor(
    val value: T,
    val children: MutableList<TreeNode<T>>
) {

    fun <S> foldNode(initial: S, f: (acc: S, treeNode: TreeNode<T>, currentIndices: List<Int>) -> S): S {
        val nodeAndIndicesStack: Stack<Pair<TreeNode<T>, List<Int>>> = Stack()
        nodeAndIndicesStack += this to emptyList()
        var acc = initial

        while (nodeAndIndicesStack.isNotEmpty()) {
            val (treeNode, indices) = nodeAndIndicesStack.pop()
            acc = f(acc, treeNode, indices)
            treeNode.children.withIndex().reversed()
                .forEach { (index, childTreeNode) -> nodeAndIndicesStack += childTreeNode to indices.plus(index) }
        }

        return acc
    }

    fun <S> fold(initial: S, f: (acc: S, element: T, currentIndices: List<Int>) -> S): S {
        return foldNode(initial) { acc, treeNode, currentIndices -> f(acc, treeNode.value, currentIndices) }
    }

    fun <S> mapNode(f: (TreeNode<T>) -> S): TreeNode<S> {
        val initial: TreeNode<S> = leafOf(f(this))
        return foldNode(initial) { acc, treeNode, currentIndices ->
            val level = currentIndices.size
            if (level == 0) acc else {
                val newTreeNode = leafOf(f(treeNode))
                acc.getOrNull(currentIndices.take(currentIndices.size - 1))
                    ?.children?.add(newTreeNode)
                acc
            }
        }
    }

    fun <S> map(f: (T) -> S): TreeNode<S> = mapNode { treeNode -> f(treeNode.value) }

    fun <S> forEachNode(f: (TreeNode<T>) -> S) {
        mapNode(f)
    }

    fun <S> forEach(f: (T) -> S) = forEachNode { treeNode -> f(treeNode.value) }

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

    fun filter(predicate: (T) -> Boolean): TreeNode<T>? = filterByNodeCondition { treeNode -> predicate(treeNode.value) }

    fun findByNodeCondition(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>? {
        var resultTreeNode: TreeNode<T>? = null
        this.forEachNode { treeNode ->
            if (predicate(treeNode) && resultTreeNode == null) {
                resultTreeNode = treeNode
            }
        }
        return resultTreeNode
    }

    fun find(predicate: (T) -> Boolean): TreeNode<T>? = findByNodeCondition { treeNode -> predicate(treeNode.value) }

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

fun <T> nodeOf(value: T, children: MutableList<TreeNode<T>>): TreeNode<T> = TreeNode.of(value, children)

fun <T> leafOf(value: T): TreeNode<T> = TreeNode.of(value, mutableListOf())
