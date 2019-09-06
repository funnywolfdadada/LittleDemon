package com.funnywolf.littledemon.model

data class Paging(var isStart: Boolean, var isEnd: Boolean, var next: String)

data class PagingList<T>(val paging: Paging): ArrayList<T>()
