package kotlintree

import java.util.*

class AdjacencyList<ID, VALUE> private constructor(
    private val list: List<AdjacencyListItem<ID, VALUE>>
) : List<AdjacencyListItem<ID, VALUE>> by list {

    fun toTreeNode(): List<TreeNode<VALUE>> {
        val parentIdToChildren = list.groupBy { it.parentNodeId }.toMutableMap()

        fun buildTree(root: AdjacencyListItem<ID, VALUE>): TreeNode<VALUE> {
            val tree = TreeNode(root.value, mutableListOf())
            val rootChildren = parentIdToChildren.getOrDefault(root.selfNodeId, mutableListOf())
            parentIdToChildren.remove(root.selfNodeId)
            val stack: Stack<Pair<AdjacencyListItem<ID, VALUE>, List<Int>>> = Stack()
            rootChildren.withIndex().reversed().forEach { pair ->
                val (index, element) = pair
                stack.push(Pair(element, listOf(index)))
            }

            while (stack.isNotEmpty()) {
                val (element, indexes) = stack.pop()
                val newTree = TreeNode(element.value, mutableListOf())
                tree.findSubTreeByIndexes(indexes.take(indexes.size - 1))
                    ?.children?.add(newTree)
                val children = parentIdToChildren.getOrDefault(element.selfNodeId, mutableListOf())
                parentIdToChildren.remove(element.selfNodeId)
                children.withIndex().reversed().forEach { pair ->
                    val (index, childElement) = pair
                    stack.push(Pair(childElement, indexes.plus(index)))
                }
            }
            return tree
        }

        val rootDataList = parentIdToChildren[null] ?: listOf()
        parentIdToChildren.remove(null)
        return rootDataList.map { root -> buildTree(root) }
    }

    companion object {
        fun <ID, VALUE> of(vararg list: AdjacencyListItem<ID, VALUE>) = AdjacencyList(list.toList())

        fun <ID, VALUE> of(vararg list: Pair<ID?, VALUE>, getSelfNodeId: (VALUE) -> ID) =
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
}

data class AdjacencyListItem<ID, VALUE>(
    val parentNodeId: ID?,
    val selfNodeId: ID?,
    val value: VALUE
) {

    companion object {
        // TODO need?
        fun <ID, VALUE> of(triple: Triple<ID?, ID, VALUE>) =
            AdjacencyListItem(triple.first, triple.second, triple.third)
    }
}
