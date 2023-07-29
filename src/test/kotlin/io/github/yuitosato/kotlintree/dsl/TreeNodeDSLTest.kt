package io.github.yuitosato.kotlintree.dsl

import io.github.yuitosato.kotlintree.TreeNode
import io.github.yuitosato.kotlintree.leafOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TreeNodeDSLTest : DescribeSpec({
    describe("nodeOf") {
        it("returns a tree node in DSL style") {
            val node = nodeOf(1) {
                nodeOf(11) {
                    nodeOf(111) {
                        leafOf(1111)
                        leafOf(1112)
                    }
                    leafOf(112)
                }
                nodeOf(12) {
                    leafOf(121)
                }
            }
            node shouldBe TreeNode.of(
                1,
                listOf(
                    TreeNode.of(
                        11,
                        listOf(
                            TreeNode.of(
                                111,
                                listOf(
                                    leafOf(1111),
                                    leafOf(1112)
                                )
                            ),
                            leafOf(112)
                        )
                    ),
                    TreeNode.of(
                        12,
                        listOf(
                            leafOf(121)
                        )
                    )
                )
            )
        }
    }
})
