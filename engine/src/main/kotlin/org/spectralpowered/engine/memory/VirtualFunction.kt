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

package org.spectralpowered.engine.memory

import com.sun.jna.Function
import com.sun.jna.Pointer
import kotlin.reflect.KClass

class VirtualFunction<T>(
    private val address: Long,
    private val returnType: KClass<*>,
    private val vtableAddress: Long
) {

    private val function = Function.getFunction(Pointer(address))!!

    @Suppress("UNCHECKED_CAST")
    operator fun invoke(vararg args: Any?): T {
        return function.invoke(returnType.java, arrayOf(Pointer(vtableAddress), *args)) as T
    }

}