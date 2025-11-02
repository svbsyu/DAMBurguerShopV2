package com.svbsyucorp.damburguershopv2.domain

data class CategoryModel(
    var id: Int = 0,
    var title: String = ""
) {
    constructor() : this(0, "")
}