package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TreeNodeTest : DescribeSpec({
    describe("foldTree") {
        it("folds a tree node and returns flatten list") {
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

            val actual = tree.foldTree(emptyList<Int>()) { acc, treeNode, _ ->
                acc + treeNode.value
            }

            actual shouldBe listOf(1, 11, 12, 111)
        }

        it("folds a tree node and returns indices list") {
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

            // flatten
            val actual = tree.foldTree(mutableListOf<List<Int>>()) { acc, _, currentIndices ->
                acc.add(currentIndices)
                acc
            }.toList()

            actual shouldBe listOf(
                listOf(),
                listOf(0),
                listOf(1),
                listOf(0, 0)
            )
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
                    TreeNode(12, mutableListOf())
                )
            )

            val actual = tree.filter { it < 1 }

            actual shouldBe null
        }
    }
})
