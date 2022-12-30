package kotlintree

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PathEnumerationListItemTest : DescribeSpec({
    describe("getParentNodeId") {
        it("gets a parent node id") {
            PathEnumerationListItem(111, listOf(1, 11, 111), 111).getParentNodeId() shouldBe 11
        }
    }
})
