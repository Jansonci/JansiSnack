package com.example.jetsnack.model


data class User(
    val name: String,
    val motto: String,
    val liked: Int,
//    val age: Int,
    val follower: Int,
    val following: Int,
//    val job: String,
//    val location: String,
    val preferences: List<Filter>,
    val info: List<String>,
    val collections: List<Snack>,
//    val posts: List<String>,
//    val likes: List<String>
    ) {
}

val Kygo = User(
    "Kygo",
    "Love what you love.",
    3,
//    28,
    139,
    20,
    listOf(
        Filter(name = "Organic"),
        Filter(name = "Gluten-free"),
        Filter(name = "Dairy-free"),
        Filter(name = "Sweet"),
        Filter(name = "Savory") ,
        Filter(name = "House")

    ),
    listOf("男","21岁","挪威","电子音乐制作人"),

    listOf(snacks[0],snacks[1], snacks[2]),
//    listOf(""),
//    listOf("")
)