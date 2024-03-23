package com.example.ferreexpress.Domain

public class SliderItems {
    private var url: String = ""

    constructor()
    constructor(url: String) {
        this.url = url
    }


    fun getUrl(): String {
        return url
    }

    fun setUrl(url: String) {
        this.url = url
    }
}