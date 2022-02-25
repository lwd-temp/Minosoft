/*
 * Minosoft
 * Copyright (C) 2020-2022 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.data.registries.other.containers.slots.equipment

import de.bixilon.minosoft.data.inventory.InventorySlots
import de.bixilon.minosoft.data.inventory.stack.ItemStack
import de.bixilon.minosoft.data.registries.items.armor.ArmorItem
import de.bixilon.minosoft.data.registries.other.containers.Container

object HeadSlotType : EquipmentSlotType {

    override fun canPut(container: Container, slot: Int, stack: ItemStack): Boolean {
        val item = stack.item.item
        // ToDo: Carved pumpkin
        if (item !is ArmorItem) {
            return false
        }
        return item.equipmentSlot == InventorySlots.EquipmentSlots.HEAD
    }
}
