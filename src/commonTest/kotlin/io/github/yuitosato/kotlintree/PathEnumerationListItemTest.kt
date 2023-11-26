package io.github.yuitosato.kotlintree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathEnumerationListItemTest : DescribeSpec({

    describe("init") {
        it("should throw a exception when a path is empty") {
            val exception = shouldThrow<Exception> { PathEnumerationListItem(listOf<Int>(), 1) }
            exception.message shouldBe "A path in a path enumeration list item must not be empty."
        }
    }

    describe("getParentNodeId") {
        it("gets a parent node id") {
            PathEnumerationListItem(listOf(1, 11, 111), 111).getParentNodeId() shouldBe 11
        }
    }
})
