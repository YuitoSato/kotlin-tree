package kotlintree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AdjacencyListTest : DescribeSpec({
    describe("init") {
        it("must be unique pairs parent node id and self id in a adjacency list.") {
            val exception = shouldThrow<Exception> {
                AdjacencyList.of(
                    getSelfNodeId = { it },
                    list = listOf(
                        null to 1,
                        1 to 11,
                        1 to 11
                    )
                )
            }
            exception.message shouldBe "A pair selfNodeId and parentNodeId must be unique in a adjacency list."
        }
    }

    describe("toTreeNode") {
        it("converts from adjacency list to tree node list") {
            val actual = AdjacencyList.of(
                getSelfNodeId = { it },
                list = listOf(
                    null to 1,
                    1 to 11,
                    11 to 111,
                    1 to 12
                )
            ).toTreeNode()

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
