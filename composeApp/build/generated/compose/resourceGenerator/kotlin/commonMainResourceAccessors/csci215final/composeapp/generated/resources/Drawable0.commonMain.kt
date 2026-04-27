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

@delegate:ResourceContentHash(-1_810_742_532)
internal val Res.drawable.checkbox: DrawableResource by lazy {
      DrawableResource("drawable:checkbox", setOf(
        ResourceItem(setOf(), "${MD}drawable/checkbox.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(11_821_879)
internal val Res.drawable.checkmark: DrawableResource by lazy {
      DrawableResource("drawable:checkmark", setOf(
        ResourceItem(setOf(), "${MD}drawable/checkmark.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(585_966_079)
internal val Res.drawable.checkmarkAppLogo: DrawableResource by lazy {
      DrawableResource("drawable:checkmarkAppLogo", setOf(
        ResourceItem(setOf(), "${MD}drawable/checkmarkAppLogo.png", -1, -1),
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

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("checkbox", Res.drawable.checkbox)
  map.put("checkmark", Res.drawable.checkmark)
  map.put("checkmarkAppLogo", Res.drawable.checkmarkAppLogo)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("edit", Res.drawable.edit)
}
