/*
 * Minosoft
 * Copyright (C) 2020-2023 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.config.profile.delegate.types.list

import de.bixilon.kutil.observer.list.ListObserver
import de.bixilon.minosoft.config.profile.delegate.AbstractProfileDelegate
import de.bixilon.minosoft.config.profile.profiles.Profile
import kotlin.reflect.KProperty

class ListDelegate<V>(
    override val profile: Profile,
    default: MutableList<V> = mutableListOf(),
) : ListObserver<V>(default), AbstractProfileDelegate<MutableList<V>> {

    init {
        value.addObserver { invalidate() }
    }

    override fun get() = value
    override fun set(value: MutableList<V>): MutableList<V> {
        validate(value)
        val previous = this.value.unsafe
        this.value.unsafe = value
        return previous
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: MutableList<V>) {
        super.setValue(thisRef, property, value)
        invalidate()
    }

    override fun validate(value: MutableList<V>) = Unit
}
