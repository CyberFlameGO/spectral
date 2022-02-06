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

package org.spectralpowered.engine.rs

import com.sun.jna.Pointer
import org.spectralpowered.engine.Offsets.fnConsoleRegisterCommand
import org.spectralpowered.engine.Offsets.fnConsoleWrite
import org.spectralpowered.engine.memory.deref
import org.spectralpowered.engine.memory.toAddress
import org.spectralpowered.engine.memory.vfunction
import org.spectralpowered.engine.memory.vtable
import org.spectralpowered.natives.memory.Addressed

class RSConsole(override val address: Long) : Addressed {
    val toggle by vtable<Pointer>(13)
    val write by vfunction<Pointer>(fnConsoleWrite)
}