package kotlintree

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

    fun toTreeNode(): PathEnumerationListToTreeNodeParseResult<ID, VALUE> {
        val pathToTreeNodeMap = mutableMapOf<List<ID>, TreeNode<VALUE>>()
        val rootTreeNodes = mutableListOf<TreeNode<VALUE>>()
        val parentNodeNotFoundList = mutableListOf<PathEnumerationListItem<ID, VALUE>>()

        this.sortedBy { it.getLevel() }.forEach { listItem ->
            val level = listItem.getLevel()
            if (level == 0) {
                val treeNode = TreeNode(listItem.value, mutableListOf())
                rootTreeNodes.add(treeNode)
                pathToTreeNodeMap[listItem.path] = treeNode
            } else {
                val parentNodePath = listItem.getParentNodePath()
                val parentTreeNode = pathToTreeNodeMap[parentNodePath]
                if (parentTreeNode == null) {
                    parentNodeNotFoundList.add(listItem)
                }
                val treeNode = TreeNode(listItem.value, mutableListOf())
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

        fun <ID, VALUE> of(vararg list: PathEnumerationListItem<ID, VALUE>): PathEnumerationList<ID, VALUE> =
            PathEnumerationList(list.toList())

        fun <ID, VALUE> fromTreeNode(
            getNodeId: (VALUE) -> ID,
            treeNode: TreeNode<VALUE>
        ): PathEnumerationList<ID, VALUE> {
            val list =
                treeNode.fold(emptyList<PathEnumerationListItem<ID, VALUE>>()) { acc, element, currentIndexes ->
                    val path = (0..currentIndexes.size).mapNotNull { level ->
                        treeNode.findSubTreeNodeByIndexes(currentIndexes.take(level))?.let { getNodeId(it.value) }
                    }
                    acc.plus(
                        PathEnumerationListItem(
                            path,
                            element
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

data class PathEnumerationListItem<ID, VALUE>(
    val path: List<ID>,
    val value: VALUE
) {

    init {
        require(path.isNotEmpty()) { throw Exception("A path in a path enumeration list item must not be empty.") }
    }

    fun getParentNodeId(): ID? {
        return path.getOrNull(path.lastIndex - 1)
    }

    fun getParentNodePath(): List<ID> {
        return path.take(path.size - 1)
    }

    fun getLevel(): Int { // zero-based level
        return path.size - 1
    }
}
