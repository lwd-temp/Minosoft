/*
 * Minosoft
 * Copyright (C) 2021 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */

package de.bixilon.minosoft.gui.rendering.models.unbaked.block

import de.bixilon.minosoft.data.registries.ResourceLocation
import de.bixilon.minosoft.data.registries.blocks.BlockState
import de.bixilon.minosoft.data.registries.blocks.properties.BlockProperties
import de.bixilon.minosoft.gui.rendering.models.unbaked.GenericUnbakedModel
import de.bixilon.minosoft.gui.rendering.models.unbaked.UnbakedModel
import de.bixilon.minosoft.util.KUtil.unsafeCast
import de.bixilon.minosoft.util.nbt.tag.NBTUtil.compoundCast

class MultipartRootModel(
    private val conditions: MutableMap<MutableSet<Map<BlockProperties, Set<Any>>>, MutableSet<UnbakedBlockStateModel>>,
) : RootModel {

    private fun Map<BlockProperties, Set<Any>>.matches(blockState: BlockState): Boolean {
        var matches = true

        for ((property, values) in this) {
            var singleMatches = false
            for (value in values) {
                if (blockState.properties[property] == value) {
                    singleMatches = true
                    break
                }
            }
            if (!singleMatches) {
                matches = false
                break
            }
        }

        return matches
    }

    private fun Set<Map<BlockProperties, Set<Any>>>.matchesAny(blockState: BlockState): Boolean {
        var matches = true
        for (or in this) {
            if (!or.matches(blockState)) {
                matches = false
                continue
            }
            matches = true
            break
        }
        return matches
    }

    override fun getModelForState(blockState: BlockState): UnbakedModel {
        val models: MutableSet<UnbakedBlockStateModel> = mutableSetOf()

        for ((condition, apply) in conditions) {
            if (condition.matchesAny(blockState)) {
                models += apply
            }
        }

        return UnbakedMultipartModel(models)
    }

    companion object {

        private fun getCondition(data: MutableMap<String, Any>): MutableMap<BlockProperties, Set<Any>> {
            val condition: MutableMap<BlockProperties, Set<Any>> = mutableMapOf()
            for ((propertyName, value) in data) {
                var property: BlockProperties? = null
                val values: MutableSet<Any> = mutableSetOf()

                for (propertyValue in value.toString().split("|")) {
                    val (parsedProperty, parsedValue) = BlockProperties.parseProperty(propertyName, propertyValue)
                    if (property == null) {
                        property = parsedProperty
                    }
                    values += parsedValue
                }
                condition[property!!] = values
            }
            return condition
        }

        operator fun invoke(models: Map<ResourceLocation, GenericUnbakedModel>, data: List<Any>): MultipartRootModel {
            val conditions: MutableMap<MutableSet<Map<BlockProperties, Set<Any>>>, MutableSet<UnbakedBlockStateModel>> = mutableMapOf()


            for (modelData in data) {
                check(modelData is Map<*, *>)
                val condition: MutableSet<Map<BlockProperties, Set<Any>>> = mutableSetOf()
                val applyData = modelData["apply"]!!
                val apply: MutableSet<UnbakedBlockStateModel> = mutableSetOf()
                if (applyData is Map<*, *>) {
                    apply += UnbakedBlockStateModel(models, applyData.unsafeCast())
                } else if (applyData is List<*>) {
                    for (applyModelData in applyData) {
                        apply += UnbakedBlockStateModel(models, applyModelData.unsafeCast())
                    }
                }

                modelData["when"]?.compoundCast()?.let {
                    val or = it["OR"]
                    if (or is List<*>) {
                        for (orData in or) {
                            condition += getCondition(orData.unsafeCast())
                        }
                        return@let
                    }
                    condition += getCondition(it)
                }



                conditions.getOrPut(condition) { mutableSetOf() } += apply
            }

            return MultipartRootModel(conditions)
        }
    }
}