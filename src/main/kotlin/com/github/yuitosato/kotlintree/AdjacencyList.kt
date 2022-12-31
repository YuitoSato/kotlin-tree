package com.github.yuitosato.kotlintree

import java.util.Stack

class AdjacencyList<ID, VALUE> private constructor(
    private val list: List<AdjacencyListItem<ID, VALUE>>
) : List<AdjacencyListItem<ID, VALUE>> by list {

    init {
        require(list.size == list.distinctBy { it.parentNodeId to it.selfNodeId }.size) {
            throw Exception("A pair selfNodeId and parentNodeId must be unique in a adjacency list.")
        }
    }

    data class AdjacencyListToTreeNodeParseResult<ID, VALUE>(
        val treeNodes: List<TreeNode<VALUE>>,
        val parentNodeNotFoundList: AdjacencyList<ID, VALUE>
    )

    fun toTreeNode(): AdjacencyListToTreeNodeParseResult<ID, VALUE> {
        val parentNodeIdToChildren = list.groupBy { it.parentNodeId }.toMutableMap()

        fun buildTree(root: AdjacencyListItem<ID, VALUE>): TreeNode<VALUE>? {
            var tree: TreeNode<VALUE>? = null
            val itemAndIndicesStack: Stack<Pair<AdjacencyListItem<ID, VALUE>, List<Int>>> = Stack()
            itemAndIndicesStack += root to emptyList()

            while (itemAndIndicesStack.isNotEmpty()) {
                val (listItem, indices) = itemAndIndicesStack.pop()
                val newTreeNode = leafOf(listItem.value)
                val level = indices.size
                if (level == 0) {
                    tree = newTreeNode
                } else {
                    tree?.getOrNull(indices.take(indices.size - 1))?.children?.add(newTreeNode)
                }
                val children = parentNodeIdToChildren.getOrDefault(listItem.selfNodeId, mutableListOf())
                parentNodeIdToChildren.remove(listItem.selfNodeId)
                children.withIndex().reversed().forEach { (index, child) ->
                    itemAndIndicesStack += child to indices.plus(index)
                }
            }

            return tree
        }

        val rootElements = parentNodeIdToChildren[null] ?: listOf()
        parentNodeIdToChildren.remove(null)
        val resultTreeNodes = rootElements.mapNotNull { root -> buildTree(root) }
        val parentNodeNotFoundList = of(parentNodeIdToChildren.values.flatten())

        return AdjacencyListToTreeNodeParseResult(
            resultTreeNodes,
            parentNodeNotFoundList
        )
    }

    companion object {

        fun <ID, VALUE> of(list: List<AdjacencyListItem<ID, VALUE>>) = AdjacencyList(list)

        fun <ID, VALUE> of(vararg list: AdjacencyListItem<ID, VALUE>) = AdjacencyList(list.toList())

        fun <ID, VALUE> of(getSelfNodeId: (VALUE) -> ID, list: List<Pair<ID?, VALUE>>) =
            AdjacencyList(
                list.map { (parentNodeId, value) ->
                    AdjacencyListItem(
                        parentNodeId,
                        getSelfNodeId(value),
                        value
                    )
                }
            )

        fun <ID, VALUE> fromTreeNode(
            getSelfNodeId: (VALUE) -> ID,
            treeNode: TreeNode<VALUE>
        ): AdjacencyList<ID, VALUE> {
            val initial = emptyList<AdjacencyListItem<ID, VALUE>>()
            return of(
                treeNode.fold(initial) { acc, element, currentIndices ->
                    val level = currentIndices.size
                    val parentNode = if (level != 0) {
                        treeNode.getOrNull(currentIndices.take(currentIndices.size - 1))
                    } else null
                    acc + AdjacencyListItem(
                        parentNodeId = parentNode?.value?.let(getSelfNodeId),
                        selfNodeId = getSelfNodeId(element),
                        value = element
                    )
                }
            )
        }
    }

    override fun hashCode() = this.list.hashCode()

    override fun equals(other: Any?) = this.list == other

    override fun toString() = this.list.toString()
}

data class AdjacencyListItem<ID, VALUE>(
    val parentNodeId: ID?,
    val selfNodeId: ID?,
    val value: VALUE
) {

    companion object {
        fun <ID, VALUE> of(
            parentNodeId: ID?,
            selfNodeId: ID?,
            value: VALUE
        ) = AdjacencyListItem(parentNodeId, selfNodeId, value)
    }
}
