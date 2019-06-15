package ir.elevin.azhand.customs

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ir.elevin.azhand.R
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class AutoCompleteTextViewLightLine : AppCompatAutoCompleteTextView {

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