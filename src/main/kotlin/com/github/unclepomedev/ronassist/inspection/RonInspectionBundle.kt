package com.github.unclepomedev.ronassist.inspection

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.RonInspectionBundle"

object RonInspectionBundle {
    private val INSTANCE = DynamicBundle(RonInspectionBundle::class.java, BUNDLE)

    @Nls
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String =
        INSTANCE.getMessage(key, *params)
}
