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

package org.spectralpowered.util.observable.property

import org.spectralpowered.util.lifecycle.LifecycleConnectable
import org.spectralpowered.util.lifecycle.LifecycleListener

/**
 * Extensions that allow using ObservablePropertys with the LifecycleConnectable.
 * Created by Kyle Escobar on 6/1/16.
 */

fun <T> LifecycleConnectable.bind(observable: ObservableProperty<T>, listener: (T) -> Unit) {
    connect(object : LifecycleListener {
        override fun onStart() {
            observable.add(listener)
            listener(observable.value)
        }

        override fun onStop() {
            observable.remove(listener)
        }
    })
}

fun <A, B> LifecycleConnectable.bind(
    observableA: ObservableProperty<A>,
    observableB: ObservableProperty<B>,
    action: (A, B) -> Unit
) {
    connect(object : LifecycleListener {

        val itemA = { item: A -> action(item, observableB.value) }
        val itemB = { item: B -> action(observableA.value, item) }

        override fun onStart() {
            observableA.add(itemA)
            observableB.add(itemB)
            action(observableA.value, observableB.value)
        }

        override fun onStop() {
            observableA.remove(itemA)
            observableB.remove(itemB)
        }
    })
}

fun LifecycleConnectable.bindBlind(
    vararg observables: ObservableProperty<out Any?>,
    action: () -> Unit
) {
    connect(object : LifecycleListener {

        val item = { item: Any? -> action() }

        override fun onStart() {
            for (obs in observables) {
                obs.add(item)
            }
            action()
        }

        override fun onStop() {
            for (obs in observables) {
                obs.remove(item)
            }
        }
    })
}

fun <A, B, C> LifecycleConnectable.bind(
    observableA: ObservableProperty<A>,
    observableB: ObservableProperty<B>,
    observableC: ObservableProperty<C>,
    action: (A, B, C) -> Unit
) {
    connect(object : LifecycleListener {

        val itemA = { item: A -> action(item, observableB.value, observableC.value) }
        val itemB = { item: B -> action(observableA.value, item, observableC.value) }
        val itemC = { item: C -> action(observableA.value, observableB.value, item) }

        override fun onStart() {
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
            action(observableA.value, observableB.value, observableC.value)
        }

        override fun onStop() {
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
        }
    })
}

fun <A, B, C, D> LifecycleConnectable.bind(
    observableA: ObservableProperty<A>,
    observableB: ObservableProperty<B>,
    observableC: ObservableProperty<C>,
    observableD: ObservableProperty<D>,
    action: (A, B, C, D) -> Unit
) {
    connect(object : LifecycleListener {

        val itemA = { item: A -> action(item, observableB.value, observableC.value, observableD.value) }
        val itemB = { item: B -> action(observableA.value, item, observableC.value, observableD.value) }
        val itemC = { item: C -> action(observableA.value, observableB.value, item, observableD.value) }
        val itemD = { item: D -> action(observableA.value, observableB.value, observableC.value, item) }

        override fun onStart() {
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
            observableD.add(itemD)
            action(observableA.value, observableB.value, observableC.value, observableD.value)
        }

        override fun onStop() {
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            observableD.remove(itemD)
        }
    })
}

fun <A, B, C, D, E> LifecycleConnectable.bind(
    observableA: ObservableProperty<A>,
    observableB: ObservableProperty<B>,
    observableC: ObservableProperty<C>,
    observableD: ObservableProperty<D>,
    observableE: ObservableProperty<E>,
    action: (A, B, C, D, E) -> Unit
) {
    connect(object : LifecycleListener {

        val itemA = { item: A -> action(item, observableB.value, observableC.value, observableD.value, observableE.value) }
        val itemB = { item: B -> action(observableA.value, item, observableC.value, observableD.value, observableE.value) }
        val itemC = { item: C -> action(observableA.value, observableB.value, item, observableD.value, observableE.value) }
        val itemD = { item: D -> action(observableA.value, observableB.value, observableC.value, item, observableE.value) }
        val itemE = { item: E -> action(observableA.value, observableB.value, observableC.value, observableD.value, item) }

        override fun onStart() {
            observableA.add(itemA)
            observableB.add(itemB)
            observableC.add(itemC)
            observableD.add(itemD)
            observableE.add(itemE)
            action(observableA.value, observableB.value, observableC.value, observableD.value, observableE.value)
        }

        override fun onStop() {
            observableA.remove(itemA)
            observableB.remove(itemB)
            observableC.remove(itemC)
            observableD.remove(itemD)
            observableE.remove(itemE)
        }
    })
}

inline fun <A, T> LifecycleConnectable.bindSub(observable: ObservableProperty<A>, crossinline mapper: (A) -> ObservableProperty<T>, noinline action: (T) -> Unit) {
    val obs = ObservableObservableProperty(mapper(observable.value))
    bind(observable) {
        obs.observable = mapper(it)
    }
    bind(obs, action)
}