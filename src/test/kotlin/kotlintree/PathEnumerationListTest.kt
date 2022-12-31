package kotlintree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathEnumerationListTest : DescribeSpec({

    describe("init") {
        it("must be unique paths in a path enumeration list") {
            val exception = shouldThrow<Exception> {
                PathEnumerationList.of(
                    listOf(1) to 1,
                    listOf(1, 11) to 11,
                    listOf(1, 11) to 11
                )
            }
            exception.message shouldBe "Paths must be unique in a path enumeration list."
        }
    }

    describe("toTreeNode") {
        it("converts a tree node to a path enumeration list.") {
            val pathEnumerationList = PathEnumerationList.of(
                listOf(1, 11) to 11,
                listOf(1) to 1,
                listOf(2) to 2,
                listOf(1, 11, 111) to 111,
                listOf(1, 12) to 12,
                listOf(2, 11) to 11, // duplicated
                listOf(2, 11, 211) to 211, // duplicated
                listOf(2, 111) to 111, // duplicated
                listOf(1, 99, 999) to 999, // a parent node is not found
                listOf(0, 11, 999) to 999 // a parent node is not found
            )
            val actual = pathEnumerationList.toTreeNode()

            val expectedTreeNodes = listOf(
                nodeOf(
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
                ),
                nodeOf(
                    2,
                    mutableListOf(
                        nodeOf(
                            11,
                            mutableListOf(
                                leafOf(211)
                            )
                        ),
                        leafOf(111)
                    )
                )
            )
            val expectedParentNodeNotFoundList = PathEnumerationList.of(
                listOf(1, 99, 999) to 999,
                listOf(0, 11, 999) to 999
            )

            actual shouldBe PathEnumerationList.PathEnumerationListToTreeNodeParseResult(
                expectedTreeNodes,
                expectedParentNodeNotFoundList
            )
        }
    }

    describe("fromTreeNode") {
        it("converts a tree node to a path enumeration list") {
            val treeNode = nodeOf(
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

            val actual = PathEnumerationList.fromTreeNode(
                getNodeId = { it },
                treeNode = treeNode
            )

            val expected = PathEnumerationList.of(
                listOf(1) to 1,
                listOf(1, 11) to 11,
                listOf(1, 11, 111) to 111,
                listOf(1, 12) to 12
            )

            actual shouldBe expected
        }
    }
})
