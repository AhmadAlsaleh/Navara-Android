package com.smartlife_solutions.android.navara_store

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.AddItemImagesAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.Dialogs.AddImageDialog
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import kotlinx.android.synthetic.main.activity_add_item.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class AddItemActivity : AppCompatActivity(), View.OnClickListener {

    private val MAIN_IMAGE_PICK = 1
    private val PICK_IMAGE_MULTI = 2

    private lateinit var mainImage: Bitmap
    private val addImages = ArrayList<Bitmap>()
    private var isMainImageSelected = false

    private val CAMERA_REQUEST = 1888
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var lang: JSONObject

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addItemBackIV, R.id.addCancelBTN -> onBackPressed()
            R.id.addMainImageIV -> AddImageDialog(this).show()
            R.id.addMoreImagesBTN -> addItemImages()
            R.id.addAddBTN -> sendAddRequest()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        StaticInformation().hideKeyboard(this)

        lang = Statics.getLanguageJSONObject(this).getJSONObject("addUsedItemActivity")
        // region font
        val myFont = StaticInformation().myFont(this)
        addItemTitle.typeface = myFont
        addItemTitle.text = lang.getString("title")
        addTitleTV.typeface = myFont
        addTitleTV.text = lang.getString("titleText")
        addTitleET.typeface = myFont
        addPriceTV.typeface = myFont
        addPriceTV.text = lang.getString("price")
        addPriceET.typeface = myFont
        addQuantityTV.typeface = myFont
        addQuantityTV.text = lang.getString("quantity")
        addQuantityET.typeface = myFont
        addImageTV.typeface = myFont
        addImageTV.text = lang.getString("mainImage")
        addMoreImagesBTN.typeface = myFont
        addMoreImagesBTN.text = lang.getString("addImages")
        addDescriptionTV.typeface = myFont
        addDescriptionTV.text = lang.getString("description")
        addDescriptionET.typeface = myFont
        addContactTV.typeface = myFont
        addContactTV.text = lang.getString("contactInformation")
        addOwnerNameET.typeface = myFont
        addOwnerNameET.hint = lang.getString("owner")
        addOwnerPhoneET.typeface = myFont
        addOwnerPhoneET.hint = lang.getString("phone")
        addAddBTN.typeface = myFont
        addAddBTN.text = lang.getString("add")
        addCancelBTN.typeface = myFont
        addCancelBTN.text = lang.getString("cancel")
        // endregion

        addItemBackIV.setOnClickListener(this)
        addMainImageIV.setOnClickListener(this)
        addMoreImagesBTN.setOnClickListener(this)
        addAddBTN.setOnClickListener(this)
        addCancelBTN.setOnClickListener(this)

        addImagesRV.setHasFixedSize(true)
        addImagesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        addImagesRV.adapter = AddItemImagesAdapter(this, addImages)

        addOwnerNameET.setText(intent.getStringExtra("name"))
        addOwnerPhoneET.setText(intent.getStringExtra("mobile"))
        Log.e("countryCode", intent.getStringExtra("countryCode"))

    }

    override fun onBackPressed() {
        if (addPB.visibility == View.VISIBLE) {
            return
        }
        val sureCancel = SureToDoDialog(this, lang.getString("sure"))
        sureCancel.show()
        sureCancel.setOnDismissListener {
            if (sureCancel.isTrue) {
                super.onBackPressed()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), CAMERA_PERMISSION_CODE)
        } else {
            takeNewImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, lang.getString("granted"), Toast.LENGTH_SHORT).show()
                takeNewImage()
            } else {
                Toast.makeText(this, lang.getString("denied"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun addItemImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_MULTI)
    }


    private lateinit var imageUri: Uri
    private fun takeNewImage() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Main Image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "from Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, imageUri), CAMERA_REQUEST)
    }

    fun getMainImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, MAIN_IMAGE_PICK)
    }

    private fun setMainImage(image: Bitmap) {
        isMainImageSelected = true
        mainImage = image
        addMainImageIV.setImageBitmap(image)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    MAIN_IMAGE_PICK -> setMainImage(StaticInformation().getResizedBitmap(
                            BitmapFactory
                                    .decodeStream(contentResolver
                                            .openInputStream(data?.data!!)),
                            800
                    ))

                    CAMERA_REQUEST -> setMainImage(StaticInformation()
                            .getResizedBitmap(MediaStore.Images.Media.getBitmap(contentResolver,
                                    imageUri),
                            800))

                    PICK_IMAGE_MULTI -> {
                        try {
                            var bitmap: Bitmap
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                if (data?.clipData != null) {
                                    val count = data.clipData!!.itemCount
                                    var currentItem = 0
                                    while (currentItem < count) {
                                        val imageUri = data.clipData!!.getItemAt(currentItem).uri
                                        bitmap = StaticInformation().getResizedBitmap(
                                                MediaStore.Images.Media.getBitmap(contentResolver, imageUri),
                                                800)
                                        addImages.add(bitmap)
                                        currentItem++
                                    }
                                } else if (data?.data != null) {
                                    val path = data.data
                                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                                    addImages.add(bitmap)
                                }
                            } else {
                                val path = data?.data
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                                addImages.add(bitmap)
                            }
                            addImagesRV.adapter = AddItemImagesAdapter(this, addImages)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("images", e.message)
                        }
                    }
                }
            }
        } catch (err: Exception) {
            Log.e("image", err.toString())
            Toast.makeText(this, lang.getString("wrong"), Toast.LENGTH_SHORT).show()
        }
    }

    fun removeImage(position: Int) {
        addImages.removeAt(position)
        addImagesRV.adapter = AddItemImagesAdapter(this, addImages)
    }

    private fun checkInput(): Boolean {
        if (addTitleET.text.toString().trim().isEmpty()) {
            Toast.makeText(this, lang.getString("titleVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addTitleET.text.toString().trim().length < 3) {
            Toast.makeText(this, lang.getString("titleLength"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addPriceET.text.toString().trim().isEmpty() || addPriceET.text.toString().trim().toInt() == 0) {
            Toast.makeText(this, lang.getString("priceVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addQuantityET.text.toString().trim().isEmpty() || addQuantityET.text.toString().trim().toInt() == 0) {
            Toast.makeText(this, lang.getString("quantityVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addDescriptionET.text.toString().trim().isEmpty()) {
            Toast.makeText(this, lang.getString("descriptionVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addDescriptionET.text.toString().trim().length < 3) {
            Toast.makeText(this, lang.getString("descriptionLength"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isMainImageSelected) {
            Toast.makeText(this, lang.getString("imageVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addOwnerNameET.text.toString().trim().isEmpty()) {
            Toast.makeText(this, lang.getString("nameVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (addOwnerPhoneET.text.toString().trim().isEmpty() || !StaticInformation().isPhone(addOwnerPhoneET.text.toString().trim())) {
            Toast.makeText(this, lang.getString("phoneVal"), Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

    private fun sendAddRequest() {
        if (!checkInput()) {
            return
        }
        addPB.visibility = View.VISIBLE
        addBTNsLL.visibility = View.GONE
        val newItem = JSONObject()
        newItem.put("Name", addTitleET.text.toString().trim())
        newItem.put("Price", addPriceET.text.toString())
        newItem.put("Quantity", addQuantityET.text.toString())
        newItem.put("Description", addDescriptionET.text.toString().trim())
        newItem.put("Owner", addOwnerNameET.text.toString().trim())
        newItem.put("Mobile", addOwnerPhoneET.text.toString().trim())
        newItem.put("Location", "")
        newItem.put("Thumbnail", StaticInformation().imageToString(mainImage))
        val images = JSONArray()
        for (image in addImages) {
            images.put(StaticInformation().imageToString(image))
        }
        newItem.put("Images", images)
        Log.e("new", newItem.toString())
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.POST, APIsURL().ADD_NEW_ITEM, {
            queue.cancelAll("add")
            finish()
            Toast.makeText(this, lang.getString("added"), Toast.LENGTH_SHORT).show()
        }, {
            queue.cancelAll("add")
            addPB.visibility = View.GONE
            addBTNsLL.visibility = View.VISIBLE
            Toast.makeText(this, lang.getString("try"), Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    newItem.toString().toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    null
                }
            }

        }
        request.retryPolicy = DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        request.tag = "add"
        queue.add(request)
    }
}
