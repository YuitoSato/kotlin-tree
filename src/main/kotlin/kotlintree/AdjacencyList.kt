package kotlintree

import java.util.LinkedList
import java.util.Queue

class AdjacencyList<ID, VALUE> private constructor(
    private val list: List<AdjacencyListItem<ID, VALUE>>
) : List<AdjacencyListItem<ID, VALUE>> by list {

    init {
        require(list.size == list.distinctBy { it.parentNodeId to it.selfNodeId }.size) {
            throw Exception("A pair selfNodeId and parentNodeId must be unique in a adjacency list.")
        }
    }

    fun toTreeNode(): List<TreeNode<VALUE>> {
        val parentNodeIdToChildren = list.groupBy { it.parentNodeId }.toMutableMap()

        fun buildTree(root: AdjacencyListItem<ID, VALUE>): TreeNode<VALUE>? {
            var tree: TreeNode<VALUE>? = null
            val queue: Queue<Pair<AdjacencyListItem<ID, VALUE>, List<Int>>> = LinkedList()
            queue += root to emptyList()

            while (queue.isNotEmpty()) {
                val (listItem, indexes) = queue.poll()
                val newTree = TreeNode(listItem.value, mutableListOf())
                val level = indexes.size
                if (level == 0) {
                    tree = newTree
                } else {
                    tree?.findSubTreeNodeByIndexes(indexes.take(indexes.size - 1))
                        ?.children?.add(newTree)
                }
                val children = parentNodeIdToChildren.getOrDefault(listItem.selfNodeId, mutableListOf())
                parentNodeIdToChildren.remove(listItem.selfNodeId)
                children.withIndex().forEach { (index, child) ->
                    queue += child to indexes.plus(index)
                }
            }

            return tree
        }

        val rootElements = parentNodeIdToChildren[null] ?: listOf()
        parentNodeIdToChildren.remove(null)
        return rootElements.mapNotNull { root -> buildTree(root) }
    }

    companion object {
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
    }

    override fun hashCode() = this.list.hashCode()

    override fun equals(other: Any?) = this.list == other

    override fun toString() = this.list.toString()
}

data class AdjacencyListItem<ID, VALUE>(
    val parentNodeId: ID?,
    val selfNodeId: ID?,
    val value: VALUE
)
