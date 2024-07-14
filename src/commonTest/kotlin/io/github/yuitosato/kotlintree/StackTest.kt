package io.github.yuitosato.kotlintree

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StackTest: FunSpec({
    test("pop() should return the last element and remove it from the stack") {
        val stack = Stack<Int>()
        val size = 10
        (0..size).forEach { stack += it }
        (size downTo 0).forEach { stack.pop() shouldBe it }
        stack.isNotEmpty() shouldBe false
    }
})
