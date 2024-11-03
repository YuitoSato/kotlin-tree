package io.github.yuitosato.kotlintree

import io.kotest.core.spec.style.FunSpec
import kotlin.time.Duration
import kotlin.time.measureTime

class PerformanceTest : FunSpec({
    test("map").config(enabled = false) {
        val time1 = measureAverageTimeOf10 {
            Test.treeNode.map { it + 1 }
        }
        println(time1)
    }
})

fun measureAverageTimeOf10(f: () -> Unit): Duration {
    val times = (0 until 10).map {
        measureTime(f)
    }
    return times.reduce { acc, measureDuration -> acc + measureDuration } / 10
}

object Test {
    val treeNode = AdjacencyList.of(
        (0 until 100).flatMap { parentId ->
            (0 until 10_000).map { selfId ->
                AdjacencyListItem(parentId, selfId, selfId)
            }
        }.plus(AdjacencyListItem(null, 0, 0))
    ).toTreeNode().treeNodes.first()
}
