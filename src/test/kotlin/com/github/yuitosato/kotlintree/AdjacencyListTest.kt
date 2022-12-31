package com.github.yuitosato.kotlintree

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
        it("converts an adjacency list to tree node list") {
            val actual = AdjacencyList.of(
                getSelfNodeId = { it },
                list = listOf(
                    null to 1,
                    1 to 11,
                    11 to 111,
                    1 to 12,
                    2 to 21 // parentNodeId is not found
                )
            ).toTreeNode()

            val expected = listOf(
                nodeOf(
                    1,
                    mutableListOf(
                        nodeOf(
                            11,
                            mutableListOf(leafOf(111))
                        ),
                        leafOf(12)
                    )
                )
            )

            actual.treeNodes shouldBe expected
            actual.parentNodeNotFoundList shouldBe AdjacencyList.of(
                listOf(
                    AdjacencyListItem(2, 21, 21)
                )
            )
        }
    }

    describe("fromTreeNode") {
        it("converts a tree node to an adjacency list") {
            AdjacencyList.fromTreeNode(
                getSelfNodeId = { it },
                nodeOf(
                    1,
                    mutableListOf(
                        nodeOf(
                            11,
                            mutableListOf(leafOf(111))
                        ),
                        leafOf(12)
                    )
                )
            ) shouldBe AdjacencyList.of(
                getSelfNodeId = { it },
                listOf(
                    null to 1,
                    1 to 11,
                    11 to 111,
                    1 to 12
                )
            )
        }
    }

    describe("of") {
        it("returns an instance with id pairs") {
            AdjacencyList.of(
                getSelfNodeId = { it },
                listOf(
                    null to 1,
                    1 to 11,
                    11 to 111,
                    1 to 12
                )
            ) shouldBe AdjacencyList.of(
                listOf(
                    AdjacencyListItem.of(null, 1, 1),
                    AdjacencyListItem.of(1, 11, 11),
                    AdjacencyListItem.of(11, 111, 111),
                    AdjacencyListItem.of(1, 12, 12)
                )
            )
        }
    }
})
