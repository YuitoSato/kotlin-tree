package io.github.yuitosato.kotlintree

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AdjacencyListItemTest : FunSpec({
    test("of method returns an instance") {
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
})
