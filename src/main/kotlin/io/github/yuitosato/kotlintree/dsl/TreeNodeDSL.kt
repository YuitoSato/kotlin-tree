package io.github.yuitosato.kotlintree.dsl

import io.github.yuitosato.kotlintree.MutableTreeNode
import io.github.yuitosato.kotlintree.TreeNode
import io.github.yuitosato.kotlintree.asMutable
import io.github.yuitosato.kotlintree.leafOf

/**
 * Returns a tree node in DSL style.
 *
 * nodeOf(1) {
 *     nodeOf(11) {
 *         leafOf(111)
 *         leafOf(112)
 *     }
 *     leafOf(12)
 * }
 */
fun <T> nodeOf(value: T, addChildren: MutableTreeNode<T>.() -> Unit): TreeNode<T> =
    leafOf(value).asMutable().apply(addChildren)

/**
 * Add a child node in a DSL block.
 */
fun <T> MutableTreeNode<T>.nodeOf(
    value: T,
    addChildren: MutableTreeNode<T>.() -> Unit
) {
    val node = TreeNode.of(value).asMutable().apply(addChildren)
    this.addChildNode(node)
}

/**
 * Add a child leaf node in a DSL block.
 */
fun <T> MutableTreeNode<T>.leafOf(value: T) {
    this.addChildNode(TreeNode.of(value))
}
