package ir.elevin.mykotlinapplication.Customs

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import ir.elevin.mykotlinapplication.R
import android.graphics.PorterDuff
import android.util.AttributeSet

class EditTextLightLine : AppCompatEditText {

    constructor(
            context: Context)
            : super(context)

    constructor(
            context: Context,
            attrs: AttributeSet? = null)
            : super(context, attrs)

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        this.setTextColor(AppCompatResources.getColorStateList(context, R.color.colorPrimaryText))
        this.setHintTextColor(AppCompatResources.getColorStateList(context, R.color.colorDividerDark))
        this.background.mutate().setColorFilter(resources.getColor(R.color.colorDivider), PorterDuff.Mode.SRC_IN)
    }

}