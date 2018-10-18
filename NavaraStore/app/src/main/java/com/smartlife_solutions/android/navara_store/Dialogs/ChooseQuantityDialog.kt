package com.smartlife_solutions.android.navara_store.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_choose_quantity.*
import org.json.JSONObject
import java.nio.charset.Charset

class ChooseQuantityDialog(context: Context, private var model: Any, private var isOffer: Boolean,
                           private var activity: Activity? = null) : Dialog(context) {

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choose_quantity)

        setFont()

        chooseQuantityClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

        if (isOffer) {
            setOffer()
        } else {
            setItem()
        }

        if (!isOffer) {
            quantityTotalTV.text = "Total: " + StaticInformation().formatPrice((model as ItemBasicModel).price) + " " + (model as ItemBasicModel).currencyCode
        } else {
            quantityTotalTV.text = "Total: " + StaticInformation().formatPrice((model as OfferBasicModel).unitNetPrice) + " " + (model as OfferBasicModel).currencyCode
        }

        chooseQuantityAdd.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            var quantity = chooseQuantityTV.text.toString().toInt()
            chooseQuantityTV.text = (++quantity).toString()
            if (!isOffer) {
                quantityTotalTV.text = "Total: " + (StaticInformation().formatPrice((model as ItemBasicModel).price * quantity)).toString() + " " + (model as ItemBasicModel).currencyCode
            } else {
                quantityTotalTV.text = "Total: " + (StaticInformation().formatPrice((model as OfferBasicModel).unitNetPrice * quantity)).toString() + " " + (model as OfferBasicModel).currencyCode
            }
        }

        chooseQuantityRemove.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            var quantity = chooseQuantityTV.text.toString().toInt()
            if (quantity > 1) {
                chooseQuantityTV.text = (--quantity).toString()
                if (!isOffer) {
                    quantityTotalTV.text = "Total: " + (StaticInformation().formatPrice((model as ItemBasicModel).price * quantity)).toString() + " " + (model as ItemBasicModel).currencyCode
                } else {
                    quantityTotalTV.text = "Total: " + (StaticInformation().formatPrice((model as OfferBasicModel).unitNetPrice * quantity)).toString() + " " + (model as OfferBasicModel).currencyCode
                }
            } else {
                Toast.makeText(context, "One at least", Toast.LENGTH_LONG).show()
            }
        }

        addToCartBTN.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            showLoad()
            if (isOffer) {
                val jsonObject = JSONObject()
                jsonObject.put("OfferID", (model as OfferBasicModel).id)
                jsonObject.put("Quantity", chooseQuantityTV.text.toString())
                addRequest(APIsURL().ADD_OFFER_TO_CART, jsonObject)
            } else {
                val jsonObject = JSONObject()
                jsonObject.put("ItemID", (model as ItemBasicModel).id)
                jsonObject.put("Quantity", chooseQuantityTV.text.toString())
                addRequest(APIsURL().ADD_ITEM_TO_CART, jsonObject)
            }
        }

    }

    private fun addRequest(url: String, jsonBody: JSONObject) {
        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val requestBody: String = jsonBody.toString()
        Log.e("item", requestBody)

        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> {
                    doneAdded()
                    queue.cancelAll("add")
                }, Response.ErrorListener {
            Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            hideLoad()
            queue.cancelAll("add")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer $myToken"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    requestBody.toByteArray(Charset.forName("utf-8"))
                } catch (err: Exception) {
                    null
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        request.tag = "add"
        queue.add(request)
    }

    private fun doneAdded() {
        dismiss()
        if (activity != null) {
//            activity!!.finish()
        }
        AllDoneDialog(context, true).show()
    }

    private fun showLoad() {
        addToCartBTN.visibility = View.GONE
        quantityPB.visibility = View.VISIBLE
    }

    private fun hideLoad() {
        addToCartBTN.visibility = View.VISIBLE
        quantityPB.visibility = View.GONE
    }

    private fun setFont() {
        // region offer
        val myFont = StaticInformation().myFont(context)
        chooseQuantityTitle.typeface = myFont
        offerPercentTV.typeface = myFont
        offerItemTitleTV.typeface = myFont
        offerItemDescriptionTV.typeface = myFont
        offerDiscountTV.typeface = myFont
        chooseQuantityTV.typeface = myFont
        addToCartBTN.typeface = myFont
        // endregion
        quantityTotalTV.typeface = myFont
        // region item
        itemTV.typeface = myFont
        itemPriceTitle.typeface = myFont
        itemPriceTV.typeface = myFont
        // endregion
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setOffer() {
        itemLL.visibility = View.GONE
        val offerModel = model as OfferBasicModel
        offerItemTitleTV.text = offerModel.title
        offerItemDescriptionTV.text = offerModel.shortDescription

        Picasso.with(context)
                .load(APIsURL().BASE_URL + offerModel.thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(offerItemIV)

        if ("free" == offerModel.offerType.toLowerCase()) {
            offerPercentTV.visibility = View.GONE
            offerGiftIV.visibility = View.VISIBLE
            offerDiscountTV.setTextColor(context.getColor(R.color.red_background))
            offerDiscountTV.text = "free"
        } else {
            offerPercentTV.visibility = View.VISIBLE
            offerGiftIV.visibility = View.GONE
            offerDiscountTV.setTextColor(context.getColor(R.color.green_background))
            offerDiscountTV.text = "-" + offerModel.discount + "%"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setItem() {
        offerItemLL.visibility = View.GONE
        val itemModel = model as ItemBasicModel
        itemTV.text = itemModel.name
        itemPriceTV.text = StaticInformation().formatPrice(itemModel.price) + " " + itemModel.currencyCode
        quantityTotalTV.text = "Total: " + StaticInformation().formatPrice(itemModel.price * itemModel.quantity) + " " + itemModel.currencyCode
        Picasso.with(context)
                .load(APIsURL().BASE_URL + itemModel.thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(itemIV)
    }
}