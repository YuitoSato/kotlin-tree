package io.github.yuitosato.kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TreeNodeTest : DescribeSpec({

    describe("foldNodeInternal") {
        it("folds a tree node and returns the sums of each node with indices") {
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

            val indicesList = mutableListOf<List<Int>>()

            val actual = tree.foldNodeInternal(emptyList<Int>()) { acc, treeNode, indices ->
                indicesList.add(indices)
                acc + treeNode.fold(0) { sum, ele ->
                    sum + ele
                }
            }

            actual shouldBe listOf(135, 122, 111, 12)
            indicesList shouldBe listOf(
                listOf(),
                listOf(0),
                listOf(0, 0),
                listOf(1)
            )
        }
    }

    describe("foldNode") {
        it("folds a tree node and returns the sums of each node") {
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

            val actual = tree.foldNode(emptyList<Int>()) { acc, treeNode ->
                acc + treeNode.fold(0) { sum, ele ->
                    sum + ele
                }
            }

            actual shouldBe listOf(135, 122, 111, 12)
        }
    }

    describe("fold") {
        it("folds a tree node and returns the flatten list") {
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

            val actual = tree.fold(emptyList<Int>()) { acc, ele ->
                acc + ele
            }

            actual shouldBe listOf(1, 11, 111, 12)
        }
    }

    describe("mapNode") {
        it("applies function to each node in tree node") {
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

            val actual = tree.mapNode { treeNode ->
                treeNode.fold(0) { acc, element ->
                    acc + element
                }
            }

            val expected = nodeOf(
                135,
                mutableListOf(
                    nodeOf(
                        122,
                        mutableListOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            )

            actual shouldBe expected
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

    describe("forEachNode") {
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

            val actual = mutableListOf<Int>()

            tree.forEachNode { treeNode ->
                actual.add(
                    treeNode.fold(0) { acc, element ->
                        acc + element
                    }
                )
            }

            val expected = mutableListOf(135, 122, 111, 12)

            actual shouldBe expected
        }
    }

    describe("forEach") {
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

            val actual = mutableListOf<Int>()
            tree.forEach { actual.add(it * 2) }

            val expected = listOf(2, 22, 222, 24)

            actual shouldBe expected
        }
    }

    describe("filterNode") {
        it("filters a tree node that matches a condition") {
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

            val actual = tree.filterNode { treeNode ->
                treeNode.find { it == 111 }.isNotEmpty()
            }

            val expected = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111)
                        )
                    )
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

            val actual = tree.filterNode { it.value < 1 }

            actual shouldBe null
        }
    }

    describe("filter") {
        it("filters a tree node that matches a condition") {
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
    }

    describe("findNode") {
        it("find tree nodes that matches the condition") {
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

            val actual = tree.findNode { treeNode ->
                treeNode.find { it % 2 != 0 }.isNotEmpty()
            }

            val expected = listOf(
                nodeOf(
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
                ),
                nodeOf(
                    11,
                    mutableListOf(
                        leafOf(111)
                    )
                ),
                leafOf(111),
                leafOf(13)
            )

            actual shouldBe expected
        }

        it("returns an empty list if that the tree node does not match the condition.") {
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

            val actual = tree.findNode { it.value == 999 }

            actual shouldBe emptyList()
        }
    }

    describe("find") {
        it("find tree nodes that matches a condition") {
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

            val actual = tree.find { it % 2 != 0 }

            val expected = listOf(
                nodeOf(
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
                ),
                nodeOf(
                    11,
                    mutableListOf(
                        leafOf(111)
                    )
                ),
                leafOf(111),
                leafOf(13)
            )

            actual shouldBe expected
        }
    }

    describe("nodeOf") {
        it("returns a tree node") {
            nodeOf(
                1,
                mutableListOf(
                    leafOf(11),
                    leafOf(12)
                )
            ) shouldBe TreeNode.of(
                1,
                mutableListOf(
                    TreeNode.of(
                        11,
                        mutableListOf()
                    ),
                    TreeNode.of(
                        12,
                        mutableListOf()
                    )
                )

            )
        }
    }

    describe("leafOf") {
        it("returns a tree node with empty children") {
            leafOf(1) shouldBe TreeNode.of(1, mutableListOf())
        }
    }

    describe("withIndices") {
        it("returns a tree node with indices") {
            val tree = nodeOf(
                1,
                mutableListOf(
                    nodeOf(
                        11,
                        mutableListOf(
                            leafOf(111),
                            leafOf(112)
                        )
                    ),
                    leafOf(12)
                )
            )
            tree.withIndices() shouldBe nodeOf(
                IndexedValue(listOf(), 1),
                mutableListOf(
                    nodeOf(
                        IndexedValue(listOf(0), 11),
                        mutableListOf(
                            leafOf(IndexedValue(listOf(0, 0), 111)),
                            leafOf(IndexedValue(listOf(0, 1), 112))
                        )
                    ),
                    leafOf(IndexedValue(listOf(1), 12))
                )
            )
        }
    }
})
