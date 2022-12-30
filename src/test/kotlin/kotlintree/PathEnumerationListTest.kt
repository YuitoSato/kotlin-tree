package kotlintree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathEnumerationListTest : DescribeSpec({

    describe("init") {
        it("must be unique paths in a path enumeration list") {
            val exception = shouldThrow<Exception> {
                PathEnumerationList.of(
                    PathEnumerationListItem(listOf(1), 1),
                    PathEnumerationListItem(listOf(1, 11), 11),
                    PathEnumerationListItem(listOf(1, 11), 11)
                )
            }
            exception.message shouldBe "Paths must be unique in a path enumeration list."
        }
    }

    describe("toTreeNode") {
        it("converts from a tree node to a path enumeration list.") {
            val pathEnumerationList = PathEnumerationList.of(
                PathEnumerationListItem(listOf(1, 11), 11),
                PathEnumerationListItem(listOf(1), 1),
                PathEnumerationListItem(listOf(2), 2),
                PathEnumerationListItem(listOf(1, 11, 111), 111),
                PathEnumerationListItem(listOf(1, 12), 12),
                PathEnumerationListItem(listOf(2, 11), 11), // duplicated
                PathEnumerationListItem(listOf(2, 11, 211), 211), // duplicated
                PathEnumerationListItem(listOf(2, 111), 111), // duplicated
                PathEnumerationListItem(listOf(1, 99, 999), 999), // a parent node is not found
                PathEnumerationListItem(listOf(0, 11, 999), 999) // a parent node is not found
            )
            val actual = pathEnumerationList.toTreeNode()

            val expectedTreeNodes = listOf(
                TreeNode(
                    1,
                    mutableListOf(
                        TreeNode(
                            11,
                            mutableListOf(
                                TreeNode(111, mutableListOf())
                            )
                        ),
                        TreeNode(12, mutableListOf())
                    )
                ),
                TreeNode(
                    2,
                    mutableListOf(
                        TreeNode(
                            11,
                            mutableListOf(
                                TreeNode(211, mutableListOf())
                            )
                        ),
                        TreeNode(
                            111,
                            mutableListOf()
                        )
                    )
                )
            )
            val expectedParentNodeNotFoundList = PathEnumerationList.of(
                PathEnumerationListItem(listOf(1, 99, 999), 999),
                PathEnumerationListItem(listOf(0, 11, 999), 999)
            )

            actual shouldBe PathEnumerationList.PathEnumerationListToTreeNodeParseResult(
                expectedTreeNodes,
                expectedParentNodeNotFoundList
            )
        }
    }

    describe("fromTreeNode") {
        it("converts from a tree node to a path enumeration list") {
            val treeNode = TreeNode(
                1,
                mutableListOf(
                    TreeNode(
                        11,
                        mutableListOf(
                            TreeNode(111, mutableListOf())
                        )
                    ),
                    TreeNode(12, mutableListOf())
                )
            )

            val actual = PathEnumerationList.fromTreeNode(
                getNodeId = { it },
                treeNode = treeNode
            )

            val expected = PathEnumerationList.of(
                PathEnumerationListItem(listOf(1), 1),
                PathEnumerationListItem(listOf(1, 11), 11),
                PathEnumerationListItem(listOf(1, 12), 12),
                PathEnumerationListItem(listOf(1, 11, 111), 111)
            )

            actual shouldBe expected
        }
    }
})
