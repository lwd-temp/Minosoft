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

package de.bixilon.minosoft.data.entities.mob;

import de.bixilon.minosoft.data.entities.Location;
import de.bixilon.minosoft.data.entities.Mob;
import de.bixilon.minosoft.data.entities.MobInterface;
import de.bixilon.minosoft.data.entities.meta.EntityMetaData;
import de.bixilon.minosoft.data.entities.meta.SlimeMetaData;

import java.util.UUID;

public class Slime extends Mob implements MobInterface {
    SlimeMetaData metaData;

    public Slime(int entityId, UUID uuid, Location location, short yaw, short pitch, short headYaw, EntityMetaData.MetaDataHashMap sets, int versionId) {
        super(entityId, uuid, location, yaw, pitch, headYaw);
        this.metaData = new SlimeMetaData(sets, versionId);
    }

    public Slime(int entityId, UUID uuid, Location location, short yaw, short pitch, int headYaw) {
        super(entityId, uuid, location, yaw, pitch, headYaw);
    }

    @Override
    public EntityMetaData getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(EntityMetaData metaData) {
        this.metaData = (SlimeMetaData) metaData;
    }

    @Override
    public float getWidth() {
        return 0.6F * metaData.getSize();
    }

    @Override
    public float getHeight() {
        return 0.6F * metaData.getSize();
    }

    @Override
    public int getMaxHealth() {
        return switch (metaData.getSize()) {
            case 1 -> 1;
            case 2 -> 4;
            case 4 -> 16;
            default -> 0;
        };
    }

    @Override
    public Class<? extends EntityMetaData> getMetaDataClass() {
        return SlimeMetaData.class;
    }
}