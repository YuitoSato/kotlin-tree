package io.github.yuitosato.kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TreeNodeTest : DescribeSpec({

    describe("foldNodeInternal") {
        it("folds a tree node and returns the sums of each node with indices") {
            val tree = nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            ).asMutable()

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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        122,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12)
                )
            )

            val actual = tree.map { it * 2 }

            val expected = nodeOf(
                2,
                listOf(
                    nodeOf(
                        22,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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

            val expected = listOf(135, 122, 111, 12)

            actual shouldBe expected
        }
    }

    describe("forEach") {
        it("applies function to each values in tree node") {
            val tree = nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf()
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                ),
                nodeOf(
                    11,
                    listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                listOf(
                    nodeOf(
                        11,
                        listOf(
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
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                ),
                nodeOf(
                    11,
                    listOf(
                        leafOf(111)
                    )
                ),
                leafOf(111),
                leafOf(13)
            )

            actual shouldBe expected
        }
    }

    describe("flatMapNode") {
        describe("prepend=true") {
            it("returns a tree node containing the results of applying the given [transform] function and prepend a transformed node to the original tree") {
                val tree = nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                )
                val actual = tree.flatMapNode(true) { nodeOf(it.value, listOf(leafOf(0))) }
                actual shouldBe nodeOf(
                    1,
                    listOf(
                        leafOf(0),
                        nodeOf(
                            11,
                            listOf(
                                leafOf(0),
                                nodeOf(
                                    111,
                                    listOf(
                                        leafOf(0)
                                    )
                                )
                            )
                        ),
                        nodeOf(
                            12,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            13,
                            listOf(
                                leafOf(0)
                            )
                        )
                    )
                )
            }
        }

        describe("prepend=false") {
            it("returns a tree node containing the results of applying the given [transform] function and add a transformed node to the original tree") {
                val tree = nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                )
                val actual = tree.flatMapNode(false) { nodeOf(it.value, listOf(leafOf(0))) }
                actual shouldBe nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                nodeOf(
                                    111,
                                    listOf(
                                        leafOf(0)
                                    )
                                ),
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            12,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            13,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        leafOf(0)
                    )
                )
            }
        }
    }

    describe("flatMap") {
        describe("prepend=true") {
            it("returns a tree node containing the results of applying the given [transform] function and prepend a transformed element to the original tree") {
                val tree = nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                )
                val actual = tree.flatMap(true) { nodeOf(it, listOf(leafOf(0))) }
                actual shouldBe nodeOf(
                    1,
                    listOf(
                        leafOf(0),
                        nodeOf(
                            11,
                            listOf(
                                leafOf(0),
                                nodeOf(
                                    111,
                                    listOf(
                                        leafOf(0)
                                    )
                                )
                            )
                        ),
                        nodeOf(
                            12,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            13,
                            listOf(
                                leafOf(0)
                            )
                        )
                    )
                )
            }
        }

        describe("prepend=false") {
            it("returns a tree node containing the results of applying the given [transform] function and add a transformed element to the original tree") {
                val tree = nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                )
                val actual = tree.flatMap(false) { nodeOf(it, listOf(leafOf(0))) }
                actual shouldBe nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                nodeOf(
                                    111,
                                    listOf(
                                        leafOf(0)
                                    )
                                ),
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            12,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        nodeOf(
                            13,
                            listOf(
                                leafOf(0)
                            )
                        ),
                        leafOf(0)
                    )
                )
            }
        }
    }

    describe("withLevel") {
        it("should return a tree node with zero-based levels.") {
            nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12),
                    leafOf(13)
                )
            ).withLevel() shouldBe nodeOf(
                ValueWithLevel(0, 1),
                listOf(
                    nodeOf(
                        ValueWithLevel(1, 11),
                        listOf(
                            leafOf(ValueWithLevel(2, 111))
                        )
                    ),
                    leafOf(ValueWithLevel(1, 12)),
                    leafOf(ValueWithLevel(1, 13))
                )
            )
        }
    }

    describe("toFlatList") {
        it("should flatten a tree node and return a flat list of elements ") {
            nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12),
                    leafOf(13)
                )
            ).toFlatList() shouldBe listOf(
                1,
                11,
                111,
                12,
                13
            )
        }
    }

    describe("toFlatListNode") {
        it("should flatten a tree node and return a flat list of nodes") {
            nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12),
                    leafOf(13)
                )
            ).toFlatListNode() shouldBe listOf(
                nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            11,
                            listOf(
                                leafOf(111)
                            )
                        ),
                        leafOf(12),
                        leafOf(13)
                    )
                ),
                nodeOf(
                    11,
                    listOf(
                        leafOf(111)
                    )
                ),
                leafOf(111),
                leafOf(12),
                leafOf(13)
            )
        }
    }

    describe("flatten") {
        describe("prepend=true") {
            it("should prepend child nodes in each element to each node") {
                nodeOf(
                    nodeOf(
                        1,
                        listOf(
                            leafOf(11)
                        )
                    ),
                    listOf(
                        nodeOf(
                            nodeOf(
                                2,
                                listOf(
                                    leafOf(21),
                                    leafOf(22)
                                )
                            ),
                            listOf(
                                nodeOf(
                                    nodeOf(
                                        3,
                                        listOf(
                                            leafOf(31),
                                            leafOf(32),
                                            leafOf(33)
                                        )
                                    ),
                                    listOf(
                                        leafOf(leafOf(4))
                                    )
                                )
                            )
                        )
                    )
                ).flatten(true) shouldBe nodeOf(
                    1,
                    listOf(
                        leafOf(11),
                        nodeOf(
                            2,
                            listOf(
                                leafOf(21),
                                leafOf(22),
                                nodeOf(
                                    3,
                                    listOf(
                                        leafOf(31),
                                        leafOf(32),
                                        leafOf(33),
                                        leafOf(4)
                                    )
                                )
                            )
                        )
                    )
                )
            }
        }

        describe("prepend=false") {
            it("should add child nodes in each element to the end of each node") {
                nodeOf(
                    nodeOf(
                        1,
                        listOf(
                            leafOf(11)
                        )
                    ),
                    listOf(
                        nodeOf(
                            nodeOf(
                                2,
                                listOf(
                                    leafOf(21),
                                    leafOf(22)
                                )
                            ),
                            listOf(
                                nodeOf(
                                    nodeOf(
                                        3,
                                        listOf(
                                            leafOf(31),
                                            leafOf(32),
                                            leafOf(33)
                                        )
                                    ),
                                    listOf(
                                        leafOf(leafOf(4))
                                    )
                                )
                            )
                        )
                    )
                ).flatten(false) shouldBe nodeOf(
                    1,
                    listOf(
                        nodeOf(
                            2,
                            listOf(
                                nodeOf(
                                    3,
                                    listOf(
                                        leafOf(4),
                                        leafOf(31),
                                        leafOf(32),
                                        leafOf(33)
                                    )
                                ),
                                leafOf(21),
                                leafOf(22)
                            )
                        ),
                        leafOf(11)
                    )
                )
            }
        }
    }

    describe("toMutable") {
        it("makes a deep copy of a node and returns it as MutableTreeNode.") {
            val node = nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111)
                        )
                    ),
                    leafOf(12),
                    leafOf(13)
                )
            )
            val mutableNode = node.toMutable()

            mutableNode shouldBe node
        }
    }

    describe("nodeOf") {
        it("returns a tree node") {
            nodeOf(
                1,
                listOf(
                    leafOf(11),
                    leafOf(12)
                )
            ) shouldBe TreeNode.of(
                1,
                listOf(
                    TreeNode.of(
                        11,
                        listOf()
                    ),
                    TreeNode.of(
                        12,
                        listOf()
                    )
                )

            )
        }
    }

    describe("leafOf") {
        it("returns a tree node with empty children") {
            leafOf(1) shouldBe TreeNode.of(1, listOf())
        }
    }

    describe("of") {
        it("returns a tree node with empty children") {
            TreeNode.of(1) shouldBe TreeNode.of(1, listOf())
        }
    }

    describe("withIndices") {
        it("returns a tree node with indices") {
            val tree = nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            leafOf(111),
                            leafOf(112)
                        )
                    ),
                    leafOf(12)
                )
            )
            tree.withIndices() shouldBe nodeOf(
                ValueWithIndices(listOf(), 1),
                listOf(
                    nodeOf(
                        ValueWithIndices(listOf(0), 11),
                        listOf(
                            leafOf(ValueWithIndices(listOf(0, 0), 111)),
                            leafOf(ValueWithIndices(listOf(0, 1), 112))
                        )
                    ),
                    leafOf(ValueWithIndices(listOf(1), 12))
                )
            )
        }
    }
})
