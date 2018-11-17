package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.smartlife_solutions.android.navara_store.AddItemActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import kotlinx.android.synthetic.main.dialog_choose_image.*

class AddImageDialog(var activity: AddItemActivity): Dialog(activity) {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choose_image)

        val myFont = StaticInformation().myFont(context)
        addImageTitleTV.typeface = myFont
        addImageTakeTV.typeface = myFont
        addImageSelectTV.typeface = myFont

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("dialogs").getJSONObject("addImage")
        addImageTitleTV.text = lang.getString("title")
        addImageTakeTV.text = lang.getString("take")
        addImageSelectTV.text = lang.getString("select")

        addImageTakeLL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
            activity.checkCameraPermission()
        }

        addImageSelectLL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
            activity.getMainImage()
        }

        addImageCloseIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

    }
}