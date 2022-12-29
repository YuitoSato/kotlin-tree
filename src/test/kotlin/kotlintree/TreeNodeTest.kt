package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TreeNodeTest : DescribeSpec({
    describe("foldTree") {
        it("sum") {
            val tree = AdjacencyList.of(
                null to 1,
                1 to 11,
                11 to 111,
                1 to 12
            ) { it }.toTreeNode().first()

            val actual = tree.foldTree(0) { acc, treeNode, currentIndexes ->
                acc + treeNode.value
            }

            actual shouldBe 135
        }
    }

    describe("map") {
        it("applies function to each values in tree node") {
            val tree = TreeNode(
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

            val actual = tree.map { it * 2 }

            val expected = TreeNode(
                2,
                mutableListOf(
                    TreeNode(
                        22,
                        mutableListOf(
                            TreeNode(222, mutableListOf())
                        )
                    ),
                    TreeNode(24, mutableListOf())
                )
            )

            actual shouldBe expected
        }
    }

    describe("filter") {
        it("filters a tree node that matches a condition.") {
            val tree = TreeNode(
                1,
                mutableListOf(
                    TreeNode(
                        11,
                        mutableListOf(
                            TreeNode(111, mutableListOf())
                        )
                    ),
                    TreeNode(12, mutableListOf()),
                    TreeNode(13, mutableListOf())
                )
            )

            val actual = tree.filter { it <= 12 }

            val expected = TreeNode(
                1,
                mutableListOf(
                    TreeNode(
                        11,
                        mutableListOf()
                    ),
                    TreeNode(
                        12,
                        mutableListOf()
                    )
                )
            )

            actual shouldBe expected
        }

        it("returns null if the top of the tree node does not match a condition.") {
            val tree = TreeNode(
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

            val actual = tree.filter { it < 1 }

            actual shouldBe null
        }
    }
})
