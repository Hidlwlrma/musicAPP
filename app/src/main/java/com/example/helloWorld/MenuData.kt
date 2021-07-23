package com.example.helloWorld

import java.io.Serializable

data class MenuData @JvmOverloads constructor(
    val id: String = "",
    val title: String = "",
    val album: String = "",
    val artist: String = "",
    val genre: String = "",
    val source: String = "",
    val image: String = "",
    val trackNumber: Int = 0,
    val totalTrackCount: Int = 0,
    val duration: Long = 0,
    val site: String = ""
) : Serializable