package com.example.jetsnack

import com.example.jetsnack.model.SnackRepo
import com.example.jetsnack.ui.home.Feed.FeedSections

object States {
    var mainScrollIndex: Int = 0
    var mainScrollOffset: Int = 0

    var postScrollIndex: Int = 0
    var postScrollOffset: Int = 0

    var filterScrollIndex: Int = 0
    var filterScrollOffset: Int = 0

    var snackScrollIndex = IntArray(SnackRepo.getSnacks().size)
    var snackScrollOffset = IntArray(SnackRepo.getSnacks().size)

    var previousTab: FeedSections = FeedSections.Topics
}