/*
 * Copyright (C) 2021 Spectral Powered <Kyle Escobar>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spectralpowered.util.observable.list

import org.spectralpowered.util.Disposable
import org.spectralpowered.util.collection.addSorted
import org.spectralpowered.util.invokeAll
import org.spectralpowered.util.lifecycle.LifecycleConnectable
import org.spectralpowered.util.lifecycle.LifecycleListener
import org.spectralpowered.util.observable.property.StandardObservableProperty
import org.spectralpowered.util.runAll
import java.util.*

/**
 * Allows you to observe the changes to a list.
 * Created by Kyle Escobar on 9/7/2015.
 */
class ObservableListFiltered<E>(
        source: ObservableList<E>
) : ObservableListIndicies<E>(source), Disposable {
    init {
        indexList.addAll(source.indices)
    }

    val filterObs = StandardObservableProperty<(E) -> Boolean>({ true })
    var filter by filterObs

    //binding
    val bindings = ArrayList<Pair<MutableCollection<*>, *>>()

    fun <T> bind(collection: MutableCollection<T>, element: T) {
        bindings.add(collection to element)
    }

    var connected = false
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        if (connected) return
        for ((collection, element) in bindings) {
            (collection as MutableCollection<Any?>).add(element)
        }
        connected = true
    }

    override fun dispose() {
        if (!connected) return
        for ((collection, element) in bindings) {
            collection.remove(element)
        }
        connected = false
    }

    //filtering

    init {
        filterObs.add {
            if (source.none(filter)) {
                indexList.clear()
                onReplace.invokeAll(this)
            } else {
                var passingIndex = 0
                for (fullIndex in source.indices) {
                    var previouslyPassing = false
                    while (passingIndex < indexList.size) {
                        if (indexList[passingIndex] > fullIndex) {
                            previouslyPassing = false
                            break
                        }
                        if (indexList[passingIndex] == fullIndex) {
                            previouslyPassing = true
                            break
                        }
                        passingIndex++
                    }

                    val passes = filter(source[fullIndex])
                    if (passes && !previouslyPassing) {
                        //add to the list
                        val addPos = indexList.addSorted(fullIndex)
                        onAdd.runAll(source[fullIndex], addPos)
                    } else if (!passes && previouslyPassing) {
                        //remove from the list
                        indexList.removeAt(passingIndex)
                        onRemove.runAll(source[fullIndex], passingIndex)
                    }
                }
            }
            onUpdate.invokeAll(this)
        }
        bind(source.onAdd) { item, index ->
            val passes = filter(item)
            if (passes) {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
                val indexOf = indexList.addSorted(index)
                onAdd.runAll(item, indexOf)
                onUpdate.invokeAll(this)
            } else {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
            }
        }
        bind(source.onChange) { old, item, index ->
            val passes = filter(item)
            val indexOf = indexList.indexOf(index)
            val passed = indexOf != -1
            if (passes != passed) {
                if (passes) {
                    val insertionIndex = indexList.addSorted(index)
                    onAdd.runAll(item, insertionIndex)
                    onUpdate.invokeAll(this)
                } else {
                    indexList.removeAt(indexOf)
                    onRemove.runAll(old, indexOf)
                    onUpdate.invokeAll(this)
                }
            } else {
                if (indexOf != -1) {
                    onChange.runAll(old, item, indexOf)
                    onUpdate.invokeAll(this)
                }
            }
        }
        bind(source.onMove) { item, oldIndex, index ->
            //remove from indexList
            val oldIndexOf = indexList.indexOf(oldIndex)
            if (oldIndexOf == -1) return@bind
            for (indexIndex in indexList.indices) {
                if (indexList[indexIndex] > oldIndex) {
                    indexList[indexIndex] -= 1
                }
            }
            indexList.remove(oldIndex)

            //add back into indexList
            val passes = filter(item)
            if (passes) {
                for (indexIndex in indexList.indices) {
                    if (indexList[indexIndex] >= index) {
                        indexList[indexIndex] += 1
                    }
                }
                val indexOf = indexList.addSorted(index)
                onMove.runAll(item, oldIndexOf, indexOf)
                onUpdate.invokeAll(this)
            }
        }
        bind(source.onRemove) { item, index ->
            val oldIndexOf = indexList.indexOf(index)
            for (indexIndex in indexList.indices) {
                if (indexList[indexIndex] > index) {
                    indexList[indexIndex] -= 1
                }
            }
            if (oldIndexOf == -1) return@bind
            indexList.remove(index)
            onRemove.runAll(item, oldIndexOf)
            onUpdate.invokeAll(this)
        }
        bind(source.onReplace) {
            indexList.clear()
            for (i in source.indices) {
                val passes = filter(source[i])
                if (passes) indexList.add(i)
            }
            onReplace.invokeAll(this)
            onUpdate.invokeAll(this)
        }
        setup()
    }
}

inline fun <E> ObservableList<E>.filtering(): ObservableListFiltered<E>
        = ObservableListFiltered(this)

inline fun <E> ObservableList<E>.filtering(noinline initFilter: (E) -> Boolean): ObservableListFiltered<E>
        = ObservableListFiltered(this).apply {
    filter = initFilter
}

inline fun <E> ObservableList<E>.filtering(lifecycle: LifecycleConnectable): ObservableListFiltered<E> {
    val list = ObservableListFiltered(this)
    lifecycle.connect(object : LifecycleListener {
        override fun onStart() {
            list.setup()
        }

        override fun onStop() {
            list.dispose()
        }
    })
    return list
}

inline fun <E> ObservableList<E>.filtering(lifecycle: LifecycleConnectable, noinline initFilter: (E) -> Boolean): ObservableListFiltered<E> {
    val list = ObservableListFiltered(this).apply {
        filter = initFilter
    }
    lifecycle.connect(object : LifecycleListener {
        override fun onStart() {
            list.setup()
        }

        override fun onStop() {
            list.dispose()
        }
    })
    return list
}