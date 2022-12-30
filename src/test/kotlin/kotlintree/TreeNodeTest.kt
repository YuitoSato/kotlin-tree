package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlintree.TreeNode.Companion.leafOf
import kotlintree.TreeNode.Companion.nodeOf

class TreeNodeTest : DescribeSpec({
    describe("foldTree") {
        it("folds a tree node and returns flatten list") {
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            )

            val actual = tree.foldTree(emptyList<Int>()) { acc, treeNode, _ ->
                acc + treeNode.value
            }

            actual shouldBe listOf(1, 11, 12, 111)
        }

        it("folds a tree node and returns indices list") {
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
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
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            )

            val actual = tree.map { it * 2 }

            val expected = nodeOf(
                2,
                mutableListOf(
                    nodeOf(
                        22,
                        mutableListOf(
                            leafOf(222)
                        )
                    ),
                    leafOf(24)
                )
            )

            actual shouldBe expected
        }
    }

    describe("filter") {
        it("filters a tree node that matches a condition.") {
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12),
                    leafOf(13)
                )
            )

            val actual = tree.filter { it <= 12 }

            val expected = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf()
                    ),
                    leafOf(12)
                )
            )

            actual shouldBe expected
        }

        it("returns null if the top of the tree node does not match a condition.") {
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            )

            val actual = tree.filter { it < 1 }

            actual shouldBe null
        }
    }
})
