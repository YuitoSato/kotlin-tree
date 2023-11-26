package io.github.yuitosato.kotlintree


internal class Stack<E> {
    private val list = mutableListOf<E>()

    operator fun plusAssign(item: E) {
        list.add(item)
    }
    fun pop(): E {
        if (isEmpty()) {
            throw NoSuchElementException("Stack is empty.")
        }
        return list.removeAt(list.size - 1)
    }

    private fun isEmpty() = list.isEmpty()

    fun isNotEmpty() = list.isNotEmpty()

    fun size() = list.size
}
