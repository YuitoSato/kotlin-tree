package io.github.yuitosato.kotlintree

/**
 * Class for Path Enumeration Model.
 * Paths in a path enumeration list must be unique.
 */
class PathEnumerationList<ID, VALUE> private constructor(
    private val list: List<PathEnumerationListItem<ID, VALUE>>
) : List<PathEnumerationListItem<ID, VALUE>> by list {

    init {
        require(list.size == list.distinctBy { it.path }.size) {
            throw Exception("Paths must be unique in a path enumeration list.")
        }
    }

    data class PathEnumerationListToTreeNodeParseResult<ID, VALUE>(
        val treeNodes: List<TreeNode<VALUE>>,
        val parentNodeNotFoundList: PathEnumerationList<ID, VALUE>
    )

    /**
     * Converts a path enumeration list to tree nodes.
     * The result contains tree nodes and a list of parent node not found
     */
    fun toTreeNode(): PathEnumerationListToTreeNodeParseResult<ID, VALUE> {
        val pathToTreeNodeMap = mutableMapOf<List<ID>, MutableTreeNode<VALUE>>()
        val rootTreeNodes = mutableListOf<MutableTreeNode<VALUE>>()
        val parentNodeNotFoundList = mutableListOf<PathEnumerationListItem<ID, VALUE>>()

        this.sortedBy { it.getLevel() }.forEach { listItem ->
            val level = listItem.getLevel()
            if (level == 0) {
                val treeNode = MutableTreeNode.of(listItem.value)
                rootTreeNodes.add(treeNode)
                pathToTreeNodeMap[listItem.path] = treeNode
            } else {
                val parentNodePath = listItem.getParentNodePath()
                val parentTreeNode = pathToTreeNodeMap[parentNodePath]
                if (parentTreeNode == null) {
                    parentNodeNotFoundList.add(listItem)
                }
                val treeNode = MutableTreeNode.of(listItem.value)
                parentTreeNode?.children?.add(treeNode)
                pathToTreeNodeMap[listItem.path] = treeNode
            }
        }

        return PathEnumerationListToTreeNodeParseResult(
            rootTreeNodes,
            PathEnumerationList(parentNodeNotFoundList.toList())
        )
    }

    companion object {

        fun <ID, VALUE> of(list: List<PathEnumerationListItem<ID, VALUE>>) = PathEnumerationList(list)

        fun <ID, VALUE> of(vararg list: Pair<List<ID>, VALUE>): PathEnumerationList<ID, VALUE> =
            of(list.map { PathEnumerationListItem.of(it.first, it.second) })

        /**
         * Converts a tree node to a path enumeration list.
         */
        fun <ID, VALUE> fromTreeNode(
            getNodeId: (VALUE) -> ID,
            treeNode: TreeNode<VALUE>
        ): PathEnumerationList<ID, VALUE> {
            val list =
                treeNode.asMutable().foldNodeInternal(emptyList<PathEnumerationListItem<ID, VALUE>>()) { acc, node, indices ->
                    val path = (0..indices.size).mapNotNull { level ->
                        treeNode.getOrNull(indices.take(level))?.let { getNodeId(it.value) }
                    }
                    acc.plus(
                        PathEnumerationListItem(
                            path,
                            node.value
                        )
                    )
                }

            return PathEnumerationList(list)
        }
    }

    override fun hashCode() = this.list.hashCode()

    override fun equals(other: Any?) = this.list == other

    override fun toString() = this.list.toString()
}

/**
 * Class for an item in a path enumeration list.
 * A path in a path enumeration list item must not be empty.
 */
data class PathEnumerationListItem<ID, VALUE>(
    val path: List<ID>,
    val value: VALUE
) {

    init {
        require(path.isNotEmpty()) { throw Exception("A path in a path enumeration list item must not be empty.") }
    }

    /**
     * Returns a parent node id.
     */
    fun getParentNodeId(): ID? {
        return path.getOrNull(path.lastIndex - 1)
    }

    /**
     * Returns a parent node path.
     */
    fun getParentNodePath(): List<ID> {
        return path.take(path.size - 1)
    }

    /**
     * Returns zero-based level.
     */
    fun getLevel(): Int { // zero-based level
        return path.size - 1
    }

    companion object {
        fun <ID, VALUE> of(path: List<ID>, value: VALUE) = PathEnumerationListItem(path, value)
    }
}
