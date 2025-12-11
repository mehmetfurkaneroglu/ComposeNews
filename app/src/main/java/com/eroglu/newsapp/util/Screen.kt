package com.eroglu.newsapp.util

sealed class Screen(val route: String) {
    object NewsList : Screen("news_list_screen")
    object ArticleDetail : Screen("article_detail_screen")
}