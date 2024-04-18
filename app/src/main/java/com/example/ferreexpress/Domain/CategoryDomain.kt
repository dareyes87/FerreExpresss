package com.example.ferreexpress.Domain

public class CategoryDomain {
    public var title: String = ""
    public var id: Int = 0
    public var picUrl: String = ""

    constructor(title: String, id: Int, picUrl: String) {
        this.title = title
        this.id = id
        this.picUrl = picUrl
    }

    constructor()

}