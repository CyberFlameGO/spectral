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

package org.spectralpowered.runescape.api

import org.koin.dsl.bind
import org.koin.dsl.module
import org.spectralpowered.api.Client
import org.spectralpowered.api.Console
import org.spectralpowered.api.Scene

val RUNESCAPE_API_MODULE = module {
    single { RSClient() } bind Client::class
    single { RSConsole() } bind Console::class
    single { RSScene() } bind Scene::class
}