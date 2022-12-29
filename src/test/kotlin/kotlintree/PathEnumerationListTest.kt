package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathEnumerationListTest : DescribeSpec({
    describe("fromTreeNode") {
        it("converts from a path enumeration list to a tree node") {
            val treeNode = TreeNode(
                1,
                mutableListOf(
                    TreeNode(
                        11,
                        mutableListOf(
                            TreeNode(111, mutableListOf())
                        )
                    ),
                    TreeNode(12, mutableListOf()),
                )
            )

            val actual = PathEnumerationList.fromTreeNode(treeNode) { it }

            val expected = PathEnumerationList.of(
                PathEnumerationListItem(1, listOf(1), 1),
                PathEnumerationListItem(11, listOf(1, 11), 11),
                PathEnumerationListItem(12, listOf(1, 12), 12),
                PathEnumerationListItem(111, listOf(1, 11, 111), 111),
            )

            actual shouldBe expected
        }
    }
})
