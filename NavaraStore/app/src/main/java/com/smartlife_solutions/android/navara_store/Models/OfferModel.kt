package com.smartlife_solutions.android.navara_store.Models

class OfferModel(var id: String, var offerType: String, var description: String,
                 var offerAmount: String, var imageURL: String,
                 var iconURL: String, var serviceID: String,
                 var freeServiceID: String, var unitPrice: Float, var currencyCode: String = "S.P",
                 var quantity: Int = 1)