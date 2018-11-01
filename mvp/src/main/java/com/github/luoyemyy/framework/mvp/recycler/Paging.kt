package com.github.luoyemyy.framework.mvp.recycler

interface Paging {

    fun reset()

    fun next()

    fun current(): Long

    fun nextError()

    class Page : Paging {

        var pageNumber: Long = 1

        override fun reset() {
            pageNumber = 1
        }

        override fun next() {
            pageNumber++
        }

        override fun nextError() {
            pageNumber--
        }

        override fun current(): Long {
            return pageNumber
        }
    }

}