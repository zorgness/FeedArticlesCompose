package com.example.feedarticlescompose.utils

import androidx.compose.ui.graphics.Color

enum class Category(
    private val id: Int,
    private val title: String,
    private val color: Color
) {

    Sport(1, "sport", Color.Yellow),
    Manga(2, "manga", Color.Blue),
    Divers(3, "divers", Color.Gray);

    companion object {

        fun getColor(categoryId: Int): Color {
            values().forEach {category ->
                if(category.id == categoryId) {
                    return category.color
                }
            }
            return Color.White
        }

        fun getTitle(categoryId: Int): String {
            values().forEach {category ->
                if(category.id == categoryId) {
                    return category.title
                }
            }
            return ""
        }
    }
}