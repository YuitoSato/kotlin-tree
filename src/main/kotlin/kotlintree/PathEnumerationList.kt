package kotlintree

class PathEnumerationList<ID, VALUE> private constructor(
    private val list: List<PathEnumerationListItem<ID, VALUE>>
) : List<PathEnumerationListItem<ID, VALUE>> by list {

    companion object {

        fun <ID, VALUE> of(vararg list: PathEnumerationListItem<ID, VALUE>): PathEnumerationList<ID, VALUE> =
            PathEnumerationList(list.toList())

        fun <ID, VALUE> fromTreeNode(
            rootTreeNode: TreeNode<VALUE>,
            getNodeId: (VALUE) -> ID
        ): PathEnumerationList<ID, VALUE> {
            val list = rootTreeNode.fold(emptyList<PathEnumerationListItem<ID, VALUE>>()) { acc, element, currentIndexes ->
                val path = (0..currentIndexes.size).mapNotNull { level ->
                    rootTreeNode.findSubTreeByIndexes(currentIndexes.take(level))?.let { getNodeId(it.value) }
                }
                acc.plus(
                    PathEnumerationListItem(
                        getNodeId(element),
                        path,
                        element
                    )
                )
            }

            return PathEnumerationList(list)
        }
    }
}

data class PathEnumerationListItem<ID, VALUE>(
    val nodeId: ID,
    val path: List<ID>,
    val value: VALUE
)
