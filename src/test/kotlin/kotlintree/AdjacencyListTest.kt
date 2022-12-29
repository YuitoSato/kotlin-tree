package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AdjacencyListTest : DescribeSpec({
    describe("toTreeNode") {
        it("converts from adjacency list to tree node list") {
            val actual = AdjacencyList.of(
                null to 1,
                1 to 11,
                11 to 111,
                1 to 12
            ) { it }.toTreeNode()

            val expected = listOf(
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
                )
            )

            actual shouldBe expected
        }
    }
})
