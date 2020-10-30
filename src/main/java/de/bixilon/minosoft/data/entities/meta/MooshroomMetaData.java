/*
 * Minosoft
 * Copyright (C) 2020 Moritz Zwerger
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This software is not affiliated with Mojang AB, the original developer of Minecraft.
 */
package de.bixilon.minosoft.data.entities.meta;

public class MooshroomMetaData extends AnimalMetaData {

    public MooshroomMetaData(MetaDataHashMap sets, int versionId) {
        super(sets, versionId);
    }

    public MooshroomTypes getType() {
        final String defaultValue = MooshroomTypes.RED.name();
        if (versionId < 461) {
            return MooshroomTypes.byTypeName(defaultValue);
        }
        return MooshroomTypes.byTypeName(sets.getString(super.getLastDataIndex() + 1, defaultValue));
    }

    @Override
    protected int getLastDataIndex() {
        if (versionId < 461) {
            return super.getLastDataIndex();
        }
        return super.getLastDataIndex() + 1;
    }

    public enum MooshroomTypes {
        RED,
        BROWN;

        public static MooshroomTypes byTypeName(String typeName) {
            return valueOf(typeName.toUpperCase());
        }

    }
}