package io.github.yuitosato.kotlintree

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
            node shouldBe nodeOf(
                1,
                listOf(
                    nodeOf(
                        11,
                        listOf(
                            nodeOf(
                                111, listOf(
                                    leafOf(1111),
                                    leafOf(1112)
                                )
                            ),
                            leafOf(112)
                        )
                    ),
                    nodeOf(
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
