package app.cash.paparazzi.accessibility

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.Snapshot
import app.cash.paparazzi.SnapshotVerifier
import app.cash.paparazzi.TestName
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

@RunWith(TestParameterInjector::class)
class AccessibilityRenderExtensionTest(
    @TestParameter
    private val testCase: TestCase
) {
  @get:Rule
  val paparazzi = Paparazzi()

  private val context: Context
    get() = paparazzi.context

  private val testName = TestName(
      packageName = "com.sample",
      className = "AccessibilityRenderExtensionTest",
      methodName = "sampleMethod"
  )
  private val snapshot = Snapshot(
      name = "",
      testName = testName,
      timestamp = Date(),
      tags = emptyList(),
      file = null
  )

  @Test
  fun test() {
    val name = testCase.name.lowercase(Locale.US)
    val viewImage = loadImageFromResourceFile("view_source")
    val view = buildView()

    val resultBufferedImage = testCase.accessibilityRenderExtension.render(snapshot.copy(name), view, viewImage)

//  Uncomment this to generate new result images
//    generateResultImages(name, resultBufferedImage)

    compareImages(name, resultBufferedImage)
  }

  @Suppress("unused")
  private fun generateResultImages(name: String, resultBufferedImage: BufferedImage) {
    //  Used to generate the result file to compare against
    val outputFile = File(GOLDEN_IMAGE_ROOT_DIR, "${name}_result.png")
    outputFile.mkdirs()
    outputFile.createNewFile()
    ImageIO.write(resultBufferedImage, "png", outputFile)
  }

  private fun compareImages(name: String, resultBufferedImage: BufferedImage) {
    val frameHandler = SnapshotVerifier(
        maxPercentDifference = 0.1,
        rootDirectory = File(GOLDEN_IMAGE_ROOT_DIR)
    ).newFrameHandler(
        snapshot = Snapshot(
            null,
            testName = TestName(
                "golden",
                name,
                "result"),
            timestamp = Date()
        ),
        frameCount = 1,
        fps = 1,
    ).use {
      it.handle(resultBufferedImage)
    }
  }

  private fun loadImageFromResourceFile(tag: String): BufferedImage =
      ImageIO.read(File("$GOLDEN_IMAGE_ROOT_DIR/images", "$tag.png"))

  enum class TestCase(
      val accessibilityRenderExtension: AccessibilityRenderExtension,
  ) {
    DEFAULT(AccessibilityRenderExtension()),
    CUSTOM(AccessibilityRenderExtension(
        renderSettings = RenderSettings(
            renderAlpha = 200,
            renderColors = listOf(
                Color.BLUE,
                Color.RED
            ),
            textColor = Color.DARK_GRAY,
            descriptionBackgroundColor = Color(210, 210, 210),
            textSize = 20f,
            colorRectSize = 40
        )
    )),
    ;
  }

  private fun buildView() =
      LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(TextView(context).apply {
          text = "Text View Sample"
        })

        addView(View(context).apply {
          layoutParams = LinearLayout.LayoutParams(100, 100)
          contentDescription = "Content Description Sample"
        })

        addView(View(context).apply {
          layoutParams = LinearLayout.LayoutParams(100, 100).apply {
            setMarginsRelative(20, 20, 20, 20)
          }
          contentDescription = "Margin Sample"
        })

        addView(Button(context).apply {
          layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
          }
          text = "Button Sample"
        })
      }

  companion object {
    private const val GOLDEN_IMAGE_ROOT_DIR = "src/test/resources"
  }
}