/*
 * Copyright (C) 2019 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paparazzi.plugin.test

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.accessibility.AccessibilityRenderExtension
import org.junit.Rule
import org.junit.Test

class AccessibilityRenderExtensionTest {

  @get:Rule
  var paparazzi = Paparazzi().apply {
    addRenderExtension(AccessibilityRenderExtension())
  }

  @Test
  fun accessibility() {
    val context = paparazzi.context

    val root = LinearLayout(context).apply {
      orientation = VERTICAL
      gravity = CENTER

      addView(AppCompatTextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        gravity = CENTER_HORIZONTAL
        textSize = 32f
        text = "Text View Sample"
      })
      addView(View(context).apply {
        layoutParams = LayoutParams(100, 100)
        gravity = CENTER_HORIZONTAL
        setPadding(20, 20, 20, 20)
        contentDescription = "Content Description Sample"
      })
    }

    paparazzi.snapshot(root, "accessibility")
  }
}
