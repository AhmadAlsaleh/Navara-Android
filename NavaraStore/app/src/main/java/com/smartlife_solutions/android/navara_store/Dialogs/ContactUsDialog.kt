package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_contact_us.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ContactUsDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_contact_us)

        // region set default info

        try {
            val dbUser = DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0]
            contactNameET.setText(dbUser.firstName)
            contactEmailET.setText(dbUser.email)
        } catch (err: Exception) {}

        // endregion

        contactSendBtn.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (contactNameET.text.toString().isNotEmpty()
                    && StaticInformation().isEmail(contactEmailET.text.toString())
                    && contactMessageET.text.toString().isNotEmpty()) {
                val messageJSON = JSONObject()
                messageJSON.put("Name", contactNameET.text.toString())
                messageJSON.put("Email", contactEmailET.text.toString())
                messageJSON.put("Message", contactMessageET.text.toString())
                sendMessage(messageJSON)
            } else {
                if (contactNameET.text.toString().isEmpty()) {
                    Toast.makeText(context, "Enter your name please", Toast.LENGTH_SHORT).show()
                }

                if (contactEmailET.text.toString().isEmpty()) {
                    Toast.makeText(context, "Enter your email please", Toast.LENGTH_SHORT).show()
                } else {
                    if (!StaticInformation().isEmail(contactEmailET.text.toString())) {
                        Toast.makeText(context, "Check your email please", Toast.LENGTH_SHORT).show()
                    }
                }

                if (contactMessageET.text.toString().isEmpty()) {
                    Toast.makeText(context, "Enter your message please", Toast.LENGTH_SHORT).show()
                }
            }
        }

        contactClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

        // region typeface
        val myFont = StaticInformation().myFont(context)
        contactTitle.typeface = myFont
        contactNameTV.typeface = myFont
        contactNameET.typeface = myFont
        contactEmailTV.typeface = myFont
        contactEmailET.typeface = myFont
        contactMessageTV.typeface = myFont
        contactMessageET.typeface = myFont
        contactSendBtn.typeface = myFont
        // endregion

    }

    private fun sendMessage(messageJSON: JSONObject) {
        contactSendBtn.visibility = View.GONE
        contactPB.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val requestBody = messageJSON.toString()
        val request = object : StringRequest(Request.Method.POST, APIsURL().CONTACT_US_URL, Response.Listener<String> {
                    Toast.makeText(context, "Message has been sent successfully", Toast.LENGTH_SHORT).show()
                    queue.cancelAll("message")
                    dismiss()
        }, Response.ErrorListener {
            try {
                Log.e("error", it.toString() + it.networkResponse.statusCode)
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_LONG).show()
            } catch (err: Exception) {
                Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_LONG).show()
            }
            contactSendBtn.visibility = View.VISIBLE
            contactPB.visibility = View.GONE
            queue.cancelAll("message")
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    requestBody.toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
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
        request.tag = "message"
        queue.add(request)
    }

}