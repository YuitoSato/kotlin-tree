package io.github.yuitosato.kotlintree

internal class Stack<E> {
    private val deque = ArrayDeque<E>()

    inline operator fun plusAssign(item: E) = deque.addLast(item)

    inline fun pop(): E = deque.removeLast()

    inline fun isNotEmpty() = deque.isNotEmpty()

    inline fun size() = deque.size
}
