package com.svbsyucorp.damburguershopv2.domain

object FavoriteManager {
    private val favoriteItems = mutableSetOf<String>()
    
    fun addFavorite(itemTitle: String) {
        favoriteItems.add(itemTitle)
    }
    
    fun removeFavorite(itemTitle: String) {
        favoriteItems.remove(itemTitle)
    }
    
    fun isFavorite(itemTitle: String): Boolean = favoriteItems.contains(itemTitle)
    
    fun getFavorites(): Set<String> = favoriteItems.toSet()
}