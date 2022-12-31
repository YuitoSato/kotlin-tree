package com.github.yuitosato.kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AdjacencyListItemTest : DescribeSpec({
    describe("of") {
        it("returns an instance") {
            AdjacencyListItem.of(
                null,
                1,
                1
            ) shouldBe AdjacencyListItem.of(
                null,
                1,
                1
            )
        }
    }
})
