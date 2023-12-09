package io.github.yuitosato.kotlintree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AdjacencyListTest : FunSpec({
    test("init method must be unique pairs parent node id and self id in a adjacency list.") {
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


    test("toTreeNode method converts an adjacency list to tree node list") {
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
                listOf(
                    nodeOf(
                        11,
                        listOf(leafOf(111))
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

    test("fromTreeNode method converts a tree node to an adjacency list") {
        AdjacencyList.fromTreeNode(
            getSelfNodeId = { it },
            nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(leafOf(111))
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

    test("of method returns an instance with id pairs") {
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

})
