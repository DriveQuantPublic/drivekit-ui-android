package com.drivequant.drivekit.common.ui.utils

import java.lang.ref.WeakReference

class DKWeakList <T> : Iterable<T> {
    private val elements: MutableList<WeakReference<T>> = mutableListOf()

    fun add(element: T) {
        this.elements.add(WeakReference(element))
    }

    fun remove(element: T) {
        val iterator = this.elements.iterator()
        while (iterator.hasNext()) {
            iterator.next().get().let {
                if (it == null || it === iterator) {
                    iterator.remove()
                }
            }
        }
    }

    fun clear() {
        this.elements.clear()
    }

    override fun iterator(): Iterator<T> {
        return DKWeakListIterator()
    }

    private inner class DKWeakListIterator : Iterator<T> {
        private var index = 0

        override fun hasNext(): Boolean {
            while (this.index < elements.size && elements[this.index].get() == null) {
                elements.removeAt(index)
            }
            return (index < elements.size)
        }

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            val element = elements[index++].get()
            if (element == null) {
                throw NoSuchElementException()
            } else {
                return element
            }
        }
    }
}
