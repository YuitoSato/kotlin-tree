package io.github.yuitosato.kotlintree

internal class Stack<E> {
    private val deque = ArrayDeque<E>()

    operator fun plusAssign(item: E) = deque.addLast(item)

    fun pop(): E = deque.removeLast()

    fun isNotEmpty() = deque.isNotEmpty()

    fun size() = deque.size
}
