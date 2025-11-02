package com.svbsyucorp.damburguershopv2

data class Item(
    var id: String = "",
    val categoryId: String = "",
    val description: String = "",
    val extra: String = "",
    val picUrl: List<String> = emptyList(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val title: String = "",
    var isFavorite: Boolean = false
)