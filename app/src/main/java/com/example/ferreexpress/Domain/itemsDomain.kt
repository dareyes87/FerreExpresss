package com.example.ferreexpress.Domain

import java.io.Serializable

public class itemsDomain: Serializable {
    public var title: String = ""
    public var description: String = ""
    public lateinit var picUrl: ArrayList<String>
    public var price: Double = 0.0
    public var oldPrice: Double = 0.0
    public var review: Int = 0
    public var rating: Double = 0.0
    public var numberInCar: Int = 0

    constructor()
    constructor(
        title: String,
        description: String,
        picUrl: ArrayList<String>,
        price: Double,
        oldPrice: Double,
        review: Int,
        rating: Double
    ) {
        this.title = title
        this.description = description
        this.picUrl = picUrl
        this.price = price
        this.oldPrice = oldPrice
        this.review = review
        this.rating = rating
    }


}