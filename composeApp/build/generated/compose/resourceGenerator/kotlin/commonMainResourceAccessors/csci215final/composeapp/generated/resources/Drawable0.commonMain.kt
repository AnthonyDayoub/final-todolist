@file:OptIn(InternalResourceApi::class)

package csci215final.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/csci215final.composeapp.generated.resources/"

@delegate:ResourceContentHash(11_821_879)
internal val Res.drawable.checkmark: DrawableResource by lazy {
      DrawableResource("drawable:checkmark", setOf(
        ResourceItem(setOf(), "${MD}drawable/checkmark.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(379_089_144)
internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

@delegate:ResourceContentHash(314_667_795)
internal val Res.drawable.edit: DrawableResource by lazy {
      DrawableResource("drawable:edit", setOf(
        ResourceItem(setOf(), "${MD}drawable/edit.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_810_742_532)
internal val Res.drawable.empty_check: DrawableResource by lazy {
      DrawableResource("drawable:empty_check", setOf(
        ResourceItem(setOf(), "${MD}drawable/empty_check.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(585_966_079)
internal val Res.drawable.filled_check: DrawableResource by lazy {
      DrawableResource("drawable:filled_check", setOf(
        ResourceItem(setOf(), "${MD}drawable/filled_check.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_108_479_956)
internal val Res.drawable.looseLeafPaperBKGD: DrawableResource by lazy {
      DrawableResource("drawable:looseLeafPaperBKGD", setOf(
        ResourceItem(setOf(), "${MD}drawable/looseLeafPaperBKGD.jpg", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("checkmark", Res.drawable.checkmark)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("edit", Res.drawable.edit)
  map.put("empty_check", Res.drawable.empty_check)
  map.put("filled_check", Res.drawable.filled_check)
  map.put("looseLeafPaperBKGD", Res.drawable.looseLeafPaperBKGD)
}
