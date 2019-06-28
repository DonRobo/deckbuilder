package at.donrobo.model

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.BoundingBox

data class ObjectLocationProperty(
    val xProperty: DoubleProperty, val yProperty: DoubleProperty,
    val widthProperty: DoubleProperty,
    val heightProperty: DoubleProperty,
    val zIndexProperty: SimpleIntegerProperty
) {
    fun toFront() {
        zIndex = -1
    }

    constructor(x: Double, y: Double, width: Double, height: Double, zIndex: Int) : this(
        SimpleDoubleProperty(x),
        SimpleDoubleProperty(y),
        SimpleDoubleProperty(width),
        SimpleDoubleProperty(height),
        SimpleIntegerProperty(zIndex)
    )

    var x: Double
        get() = xProperty.value
        set(value) {
            xProperty.value = value
        }
    var y: Double
        get() = yProperty.value
        set(value) {
            yProperty.value = value
        }

    var width: Double
        get() = widthProperty.value
        set(value) {
            widthProperty.value = value
        }
    var height: Double
        get() = heightProperty.value
        set(value) {
            heightProperty.value = value
        }

    val bounds: BoundingBox get() = BoundingBox(x, y, width, height)

    var zIndex: Int
        get() = zIndexProperty.value
        set(value) {
            zIndexProperty.value = value
        }

}