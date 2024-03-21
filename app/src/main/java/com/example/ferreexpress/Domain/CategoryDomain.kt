package com.example.ferreexpress.Domain

public class CategoryDomain {
    public var titulo: String = ""
    public var id: Int = 0
    public var picUrl: String = ""

    constructor(titulo: String, id: Int, picUrl: String) {
        this.titulo = titulo
        this.id = id
        this.picUrl = picUrl
    }

    constructor()

}