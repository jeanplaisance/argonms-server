/*
 * ArgonMS MapleStory server emulator written in Java
 * Copyright (C) 2011-2013  GoldenKevin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package argonms.game.loading.mob;

import argonms.common.character.inventory.InventoryTools;
import argonms.common.util.input.LittleEndianByteArrayReader;
import argonms.common.util.input.LittleEndianReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import argonms.common.util.DatabaseManager;
import argonms.common.util.DatabaseManager.DatabaseType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author GoldenKevin
 */
public class KvjMobDataLoader extends MobDataLoader {
	private static final Logger LOG = Logger.getLogger(KvjMobDataLoader.class.getName());

	private static final byte
		LEVEL = 1,
		MAX_HP = 2,
		MAX_MP = 3,
		PHYSICAL_DAMAGE = 4,
		EXP = 5,
		UNDEAD = 6,
		ELEM_ATTR = 7,
		REMOVE_AFTER = 8,
		HIDE_HP = 9,
		HIDE_NAME = 10,
		HP_TAG_COLOR = 11,
		HP_TAG_BG_COLOR = 12,
		BOSS = 13,
		SELF_DESTRUCT = 14,
		LOSE_ITEM = 15,
		INVINCIBLE = 16,
		REVIVE = 17,
		FIRST_ATTACK = 18,
		ATTACK = 19,
		SKILL = 20,
		BUFF = 21,
		DELAY = 22,
		DROPS = 23,
		NO_MESOS = 24,
		QUEST_ITEM_DROPS = 25,
		DESTROY_ANIMATION = 26,
		DROP_ITEM_PERIOD = 27
	;

	private final String dataPath;

	protected KvjMobDataLoader(String wzPath) {
		this.dataPath = wzPath;
	}

	@Override
	protected void load(int mobid) {
		String id = String.format("%07d", mobid);

		MobStats stats = null;
		try {
			File f = new File(new StringBuilder(dataPath).append("Mob.wz").append(File.separator).append(id).append(".img.kvj").toString());
			if (f.exists()) {
				stats = new MobStats(mobid);
				try{
					doWork(new LittleEndianByteArrayReader(f), stats);
				}catch(SQLException e) {System.out.println("[Database Drops] Houston, we had a problem.");}
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not read KVJ data file for mob " + mobid, e);
		}
		mobStats.put(Integer.valueOf(mobid), stats);
	}

	@Override
	public boolean loadAll() {
		try {

			File root = new File(dataPath + "Mob.wz");
			for (String kvj : root.list()) {
				int mobid = Integer.parseInt(kvj.substring(0, kvj.lastIndexOf(".img.kvj")));
				MobStats stats = new MobStats(mobid);
				try {
					doWork(new LittleEndianByteArrayReader(new File(root.getAbsolutePath() + File.separatorChar + kvj)), stats);
				}catch(SQLException e) {System.out.println("[Database Drops] Houston, we had a problem.");}
				mobStats.put(Integer.valueOf(mobid), stats);

			}

			return true;
		} catch (IOException ex) {
			LOG.log(Level.WARNING, "Could not load all mob data from KVJ files.", ex);
			return false;
		}
	}

	@Override
	public boolean canLoad(int mobid) {
		if (mobStats.containsKey(mobid))
			return true;
		String id = String.format("%07d", mobid);
		File f = new File(new StringBuilder(dataPath).append("Mob.wz").append(File.separator).append(id).append(".img.kvj").toString());
		return f.exists();
	}

	private void doWork(LittleEndianReader reader, MobStats stats) throws SQLException {
		boolean db = false;
		//Gets item drops from database if available
		Connection con = DatabaseManager.getConnection(DatabaseType.STATE);
		PreparedStatement ps = con.prepareStatement("SELECT `itemid`, `chance`, `min_amount`, `max_amount` FROM `monsterdrops` WHERE `monsterid` = ?");
		ps.setInt(1, stats.getMobId());
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			db = true;
			rs.beforeFirst();
			while(rs.next()){
				stats.addItemDrop(rs.getInt("itemid"), rs.getInt("chance"), (short) rs.getInt("min_amount"), (short) rs.getInt("max_amount"));
			}
		}
		//InputStream is = new BufferedInputStream(new FileInputStream(prefFolder.getAbsolutePath() + File.separatorChar + kvj));
		//doWork(new LittleEndianStreamReader(is), stats);
		//is.close();
		for (byte now = reader.readByte(); now != -1; now = reader.readByte()) {
			switch (now) {
				case LEVEL:
					stats.setLevel(reader.readShort());
					break;
				case MAX_HP:
					stats.setMaxHp(reader.readInt());
					break;
				case MAX_MP:
					stats.setMaxMp(reader.readInt());
					break;
				case PHYSICAL_DAMAGE:
					stats.setPhysicalDamage(reader.readInt());
					break;
				case EXP:
					stats.setExp(reader.readInt());
					break;
				case UNDEAD:
					stats.setUndead();
					break;
				case ELEM_ATTR:
					stats.setElementalAttribute(reader.readNullTerminatedString());
					break;
				case REMOVE_AFTER:
					stats.setRemoveAfter(reader.readInt());
					break;
				case HIDE_HP:
					stats.setHideHp();
					break;
				case HIDE_NAME:
					stats.setHideName();
					break;
				case HP_TAG_COLOR:
					stats.setHpTagColor(reader.readByte());
					break;
				case HP_TAG_BG_COLOR:
					stats.setHpTagBgColor(reader.readByte());
					break;
				case BOSS:
					stats.setBoss();
					break;
				case SELF_DESTRUCT:
					stats.setSelfDestructHp(reader.readInt());
					break;
				case LOSE_ITEM:
					stats.addLoseItem(reader.readInt(), reader.readByte());
					break;
				case INVINCIBLE:
					stats.setInvincible();
					break;
				case REVIVE:
					stats.addSummon(reader.readInt());
					break;
				case FIRST_ATTACK:
					stats.setFirstAttack();
					break;
				case ATTACK:
					processAttack(reader, stats);
					break;
				case SKILL:
					processSkill(reader, stats);
					break;
				case BUFF:
					stats.setBuffToGive(reader.readInt());
					break;
				case DELAY:
					String name = reader.readNullTerminatedString();
					int delay = reader.readInt();
					stats.addDelay(name, delay);
					break;
				case DROPS: {
					byte amt = reader.readByte();
					for (int i = amt - 1; i >= 0; --i) {
						int itemid = reader.readInt();
						int chance = reader.readInt();
						//Breaks if the database has already populated drops for this mob, indicated by a non-zero rs row. It needs to read first, however, to advance the stream.
						if(db){ break; }
						if (InventoryTools.isArrowForBow(itemid) || InventoryTools.isArrowForCrossBow(itemid))
							stats.addItemDrop(itemid, chance, (short) 10, (short) 25);
						else
							stats.addItemDrop(itemid, chance, (short) 1, (short) 1);
					}
					break;
				}
				case NO_MESOS:
					stats.setMesoDrop(0, 0, 0);
					break;
				case QUEST_ITEM_DROPS: {
					byte amt = reader.readByte();
					for (int i = amt - 1; i >= 0; --i) {
						int itemid = reader.readInt();
						int chance = reader.readInt();
						short questId = reader.readShort();
						stats.addItemDrop(itemid, chance, (short) 1, (short) 1, questId);
					}
					break;
				}
				case DESTROY_ANIMATION:
					stats.setDestroyAnimation(reader.readByte());
					break;
				case DROP_ITEM_PERIOD:
					stats.setDropItemPeriod(reader.readByte());
					break;
			}
		}

		//Gets meso drops from database if available
		ps = con.prepareStatement("SELECT `mesoamount`, `chance` FROM `mesodrops` WHERE `monsterid` = ?");
		ps.setInt(1, stats.getMobId());
		rs = ps.executeQuery();
		if(rs.next()){
			stats.setMesoDrop(rs.getInt("chance"), (int)(rs.getInt("mesoamount") * 0.8),  (int)(rs.getInt("mesoamount")*1.2));
		}
	}

	private void processAttack(LittleEndianReader reader, MobStats stats) {
		byte attackid = reader.readByte();
		Attack a = new Attack();
		a.setDeadlyAttack(reader.readBool());
		a.setMpBurn(reader.readShort());
		a.setDiseaseSkill(reader.readByte());
		a.setDiseaseLevel(reader.readByte());
		a.setMpConsume(reader.readInt());
		stats.addAttack(attackid, a);
	}

	private void processSkill(LittleEndianReader reader, MobStats stats) {
		Skill s = new Skill();
		s.setSkill(reader.readShort());
		s.setLevel(reader.readByte());
		s.setEffectDelay(reader.readShort());
		stats.addSkill(s);
	}
}
