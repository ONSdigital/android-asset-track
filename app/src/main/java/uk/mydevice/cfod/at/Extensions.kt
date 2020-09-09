package uk.mydevice.cfod.at

import android.text.Html
import android.text.Spanned

fun String.toHtmlSpan(): Spanned =
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)

