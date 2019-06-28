package at.donrobo.model

import at.donrobo.view.cardSizeRatio
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class CollectionDeckbuilderObject(
    override val defaultWidth: Double = 300.0,
    override val defaultHeight: Double = defaultWidth / cardSizeRatio
) : DeckbuilderObject() {
    private val objectAddedListeners: MutableList<(DeckbuilderObject, ObjectLocationProperty) -> Unit> =
        LinkedList()
    private val objectRemovedListeners: MutableList<(DeckbuilderObject) -> Unit> =
        LinkedList()

    private val internalDeckbuilderObjects: MutableMap<DeckbuilderObject, ObjectLocationProperty> =
        HashMap()
    private val zIndexList: MutableList<Pair<DeckbuilderObject, ObjectLocationProperty>> =
        LinkedList()
    private var disableZListener = false
    private val zIndexChangeListener: ChangeListener<in Number> = ChangeListener { _, _, _ ->
        if (!disableZListener)
            updateZIndices()
    }

    var cardsSideBySide: Int = 5
    var gapSize: Double = 30.0

    val deckbuilderObjects: Map<DeckbuilderObject, ObjectLocationProperty> get() = internalDeckbuilderObjects

    val name: String = "Collection"

    fun addObject(
        deckbuilderObject: DeckbuilderObject,
        widthProperty: DoubleProperty = SimpleDoubleProperty(
            deckbuilderObject.defaultWidth
        ),
        heightProperty: DoubleProperty = SimpleDoubleProperty(
            deckbuilderObject.defaultHeight
        )
    ) {
        var maxYInRow = 0.0
        var xIndex = 0

        val locationProperty =
            ObjectLocationProperty(
                SimpleDoubleProperty(0.0),
                SimpleDoubleProperty(0.0),
                widthProperty,
                heightProperty,
                SimpleIntegerProperty(-1)
            )
        var intersections = getObjectsIntersecting(locationProperty)
        while (intersections.isNotEmpty()) {
            maxYInRow = max(maxYInRow, intersections.map { it.second.bounds.maxY }.max()!!)
            if (xIndex + 1 < cardsSideBySide) {
                locationProperty.x = intersections.map { it.second.bounds.maxX }.max()!! + gapSize
                xIndex++
            } else {
                locationProperty.x = 0.0
                locationProperty.y = maxYInRow + gapSize
                xIndex = 0
                maxYInRow = 0.0
            }

            intersections = getObjectsIntersecting(locationProperty)
        }

        addObject(deckbuilderObject, locationProperty)
    }

    fun getObjectsIntersecting(locationProperty: ObjectLocationProperty): List<Pair<DeckbuilderObject, ObjectLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.intersects(locationProperty.bounds) }.map { it.toPair() }

    fun getObjectsAt(x: Double, y: Double): List<Pair<DeckbuilderObject, ObjectLocationProperty>> =
        deckbuilderObjects.filter { it.value.bounds.contains(x, y) }.map { it.toPair() }

    fun addObject(deckbuilderObject: DeckbuilderObject, objectLocationProperty: ObjectLocationProperty) {
        internalDeckbuilderObjects[deckbuilderObject] = objectLocationProperty
        zIndexList.add(deckbuilderObject to objectLocationProperty)
        objectLocationProperty.zIndexProperty.addListener(zIndexChangeListener)
        updateZIndices()

        objectAddedListeners.forEach { it(deckbuilderObject, objectLocationProperty) }
    }

    fun removeObject(deckbuilderObject: DeckbuilderObject): ObjectLocationProperty? {
        val returnValue = internalDeckbuilderObjects.remove(deckbuilderObject)
        zIndexList.removeIf { it.first == deckbuilderObject }
        returnValue?.zIndexProperty?.removeListener(zIndexChangeListener)
        updateZIndices()

        if (returnValue != null)
            objectRemovedListeners.forEach { it(deckbuilderObject) }

        return returnValue
    }

    fun addObjectAddedListener(listener: (DeckbuilderObject, ObjectLocationProperty) -> Unit) {
        objectAddedListeners += listener
    }

    fun addObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners += listener
    }

    fun removeObjectAddedListener(listener: (DeckbuilderObject, ObjectLocationProperty) -> Unit) {
        objectAddedListeners -= listener
    }

    fun removeObjectRemovedListener(listener: (DeckbuilderObject) -> Unit) {
        objectRemovedListeners -= listener
    }

    fun objectAt(
        position: Point2D,
        except: DeckbuilderObject? = null
    ): Pair<DeckbuilderObject, ObjectLocationProperty>? {
        return internalDeckbuilderObjects.filter {
            it.key != except && it.value.bounds.contains(position)
        }.minBy { it.component2().zIndex }?.toPair()
    }

    private fun updateZIndices() {
        disableZListener = true
        zIndexList.sortBy { it.second.zIndex }
        zIndexList.forEachIndexed { index, pair -> pair.second.zIndex = index }
        disableZListener = false
    }

    fun dropObject(droppingObj: DeckbuilderObject, location: ObjectLocationProperty, position: Point2D) {
        val droppedOn = objectAt(position, except = droppingObj)
        if (droppedOn == null) {
            if (!internalDeckbuilderObjects.containsKey(droppingObj)) {
                addObject(droppingObj, location)
            } else {
                //do nothing
            }
        } else {
            when (val droppedOnObj = droppedOn.first) {
                is CollectionDeckbuilderObject -> {
                    if (droppingObj is CardDeckbuilderObject) {
                        removeObject(droppingObj)
                        droppedOnObj.addObject(droppingObj)
                    } else if (droppingObj is CollectionDeckbuilderObject) {
                        removeObject(droppingObj)
                        droppingObj.deckbuilderObjects.keys.forEach {
                            droppedOnObj.addObject(it)
                        }
                    }
                }
                is CardDeckbuilderObject -> {
                    if (droppingObj is CardDeckbuilderObject) {
                        removeObject(droppingObj)
                        removeObject(droppedOnObj)

                        val collectionDeckbuilderObject = CollectionDeckbuilderObject()
                        collectionDeckbuilderObject.addObject(droppingObj)
                        collectionDeckbuilderObject.addObject(droppedOnObj)

                        addObject(
                            collectionDeckbuilderObject,
                            ObjectLocationProperty(
                                droppedOn.second.x,
                                droppedOn.second.y,
                                collectionDeckbuilderObject.defaultWidth,
                                collectionDeckbuilderObject.defaultHeight,
                                -1
                            )
                        )
                    } else {
                        //do nothing
                    }
                }
                else -> throw RuntimeException("Not implemented: $droppedOnObj")
            }
        }
    }

}