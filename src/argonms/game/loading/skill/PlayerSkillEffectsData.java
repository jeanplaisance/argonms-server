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

package argonms.game.loading.skill;

import argonms.common.character.PlayerStatusEffect;
import argonms.common.character.Skills;
import argonms.common.field.MonsterStatusEffect;
import argonms.common.loading.StatusEffectsData.BuffsData;
import argonms.common.loading.StatusEffectsData.MonsterStatusEffectsData;
import argonms.common.util.Rng;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author GoldenKevin
 */
public class PlayerSkillEffectsData extends BuffsData implements MonsterStatusEffectsData {
	private int skillId;
	private final byte level;
	private short mpCon;
	private short hpCon;
	private int x;
	private int y;
	private int z;
	private short damage;
	private Point lt;
	private Point rb;
	private byte mobCount;
	private short prop;
	private byte mastery;
	private short cooltime;
	private short range;
	private byte attackCount;
	private byte bulletCount;
	private int itemCon;
	private byte itemConCount;
	private short bulletCon;
	private short moneyCon;
	private final Set<MonsterStatusEffect> monsterDiseases;
	private boolean isFreeze;
	private short hp, mp;

	protected PlayerSkillEffectsData(int skillid, byte level) {
		super(skillid);
		this.setSkillId(skillid);
		this.level = level;
		this.prop = 100;
		this.damage = 100;
		this.monsterDiseases = EnumSet.noneOf(MonsterStatusEffect.class);
		switch (skillid) { //for them skills that don't have x or y
			case Skills.BEHOLDER:
			case Skills.IFRIT:
			case Skills.SUMMON_DRAGON:
			case Skills.BAHAMUT:
			case Skills.OCTOPUS:
			case Skills.GAVIOTA:
			case Skills.WRATH_OF_THE_OCTOPI:
				effects.add(PlayerStatusEffect.SUMMON);
				break;
			case Skills.HOMING_BEACON:
				effects.add(PlayerStatusEffect.HOMING_BEACON);
				break;
			case Skills.FP_SEAL:
			case Skills.IL_SEAL:
				monsterDiseases.add(MonsterStatusEffect.SEAL);
				break;
			case Skills.DOOM:
				monsterDiseases.add(MonsterStatusEffect.DOOM);
				break;
			case Skills.SHADOW_WEB:
				monsterDiseases.add(MonsterStatusEffect.SHADOW_WEB);
				break;
			case Skills.NL_NINJA_AMBUSH:
			case Skills.SHADOWER_NINJA_AMBUSH:
				monsterDiseases.add(MonsterStatusEffect.NINJA_AMBUSH);
				break;
			case Skills.SWORD_COMA:
			case Skills.AXE_COMA:
			case Skills.SHOUT:
			case Skills.CHARGED_BLOW:
			case Skills.ASSAULTER:
			case Skills.BOOMERANG_STEP:
			case Skills.BACKSPIN_BLOW:
			case Skills.DOUBLE_UPPERCUT:
			case Skills.DEMOLITION:
			case Skills.SNATCH:
			case Skills.BARRAGE:
			case Skills.BLANK_SHOT:
				monsterDiseases.add(MonsterStatusEffect.STUN);
				break;
			case Skills.ICE_STRIKE:
			case Skills.IL_BLIZZARD:
			case Skills.ICE_SPLITTER:
				monsterDiseases.add(MonsterStatusEffect.FREEZE);
				isFreeze = true;
				break;
			case Skills.PARALYZE:
				monsterDiseases.add(MonsterStatusEffect.SPEED);
				break;
			case Skills.POISON_BREATH:
			case Skills.FP_ELEMENT_COMPOSITION:
			case Skills.POISON_MIST:
			case Skills.VENOMOUS_STAR:
			case Skills.VENOMOUS_STAB:
				monsterDiseases.add(MonsterStatusEffect.POISON);
				break;
			case Skills.HYPNOTIZE:
				monsterDiseases.add(MonsterStatusEffect.INERTMOB);
				break;
			case Skills.MYSTIC_DOOR:
				effects.add(PlayerStatusEffect.MYSTIC_DOOR);
				break;
			case Skills.HIDE:
				effects.add(PlayerStatusEffect.HIDE);
				//kinda hacky, but hide doesn't have a duration
				setDuration(60 * 120 * 1000);
				break;
		}
	}

	@Override
	public EffectSource getSourceType() {
		return EffectSource.PLAYER_SKILL;
	}

	protected void setMpConsume(short mpCon) {
		this.mpCon = mpCon;
	}

	protected void setHpConsume(short hpCon) {
		this.hpCon = hpCon;
	}

	protected void setX(int x) {
		if (x != 0) {
			this.x = x;
			switch (getDataId()) {
				case Skills.MAGIC_GUARD:
					effects.add(PlayerStatusEffect.MAGIC_GUARD);
					break;
				case Skills.INVINCIBLE:
					effects.add(PlayerStatusEffect.INVINCIBLE);
					break;
				case Skills.DARK_SIGHT:
					effects.add(PlayerStatusEffect.DARKSIGHT);
					break;
				case Skills.PICK_POCKET:
					effects.add(PlayerStatusEffect.PICKPOCKET);
					break;
				case Skills.MESO_GUARD:
					effects.add(PlayerStatusEffect.MESO_GUARD);
					break;
				case Skills.MESO_UP:
					effects.add(PlayerStatusEffect.MESO_UP);
					break;
				case Skills.SHADOW_PARTNER:
					effects.add(PlayerStatusEffect.SHADOW_PARTNER);
					break;
				case Skills.BOW_SOUL_ARROW:
				case Skills.XBOW_SOUL_ARROW:
					effects.add(PlayerStatusEffect.SOUL_ARROW);
					break;
				case Skills.SWORD_ICE_CHARGE:
				case Skills.BW_BLIZZARD_CHARGE:
					monsterDiseases.add(MonsterStatusEffect.FREEZE);
					//intentional fallthrough to PlayerStatusEffect.CHARGE
				case Skills.SWORD_FIRE_CHARGE:
				case Skills.BW_FLAME_CHARGE:
				case Skills.SWORD_THUNDER_CHARGE:
				case Skills.BW_LIGHTNING_CHARGE:
				case Skills.SWORD_HOLY_CHARGE:
				case Skills.BW_DIVINE_CHARGE:
					effects.add(PlayerStatusEffect.CHARGE);
					break;
				case Skills.CRUSADER_SWORD_BOOSTER:
				case Skills.AXE_BOOSTER:
				case Skills.PAGE_SWORD_BOOSTER:
				case Skills.BW_BOOSTER:
				case Skills.SPEAR_BOOSTER:
				case Skills.POLE_ARM_BOOSTER:
				case Skills.FP_SPELL_BOOSTER:
				case Skills.IL_SPELL_BOOSTER:
				case Skills.BOW_BOOSTER:
				case Skills.XBOW_BOOSTER:
				case Skills.CLAW_BOOSTER:
				case Skills.DAGGER_BOOSTER:
				case Skills.KNUCKLER_BOOSTER:
				case Skills.GUN_BOOSTER:
					effects.add(PlayerStatusEffect.BOOSTER);
					break;
				case Skills.SPEED_INFUSION:
					effects.add(PlayerStatusEffect.SPEED_INFUSION);
					break;
				case Skills.CONCENTRATE:
					effects.add(PlayerStatusEffect.CONCENTRATE);
					break;
				case Skills.DASH:
					effects.add(PlayerStatusEffect.DASH_SPEED);
					break;
				case Skills.FIGHTER_POWER_GUARD:
				case Skills.PAGE_POWER_GUARD:
					effects.add(PlayerStatusEffect.POWER_GUARD);
					break;
				case Skills.SPEARMAN_HYPER_BODY:
				case Skills.GM_HYPER_BODY:
					effects.add(PlayerStatusEffect.HYPER_BODY_HP);
					break;
				case Skills.RECOVERY:
					effects.add(PlayerStatusEffect.RECOVERY);
					break;
				case Skills.COMBO:
					effects.add(PlayerStatusEffect.COMBO);
					break;
				case Skills.MONSTER_RIDING:
				case Skills.BATTLE_SHIP:
					effects.add(PlayerStatusEffect.MONSTER_RIDING);
					break;
				case Skills.DRAGON_BLOOD:
					effects.add(PlayerStatusEffect.DRAGON_BLOOD);
					break;
				case Skills.HERO_MAPLE_WARRIOR:
				case Skills.PALADIN_MAPLE_WARRIOR:
				case Skills.DARK_KNIGHT_MAPLE_WARRIOR:
				case Skills.FP_MAPLE_WARRIOR:
				case Skills.IL_MAPLE_WARRIOR:
				case Skills.BISHOP_MAPLE_WARRIOR:
				case Skills.BOW_MASTER_MAPLE_WARRIOR:
				case Skills.XBOW_MASTER_MAPLE_WARRIOR:
				case Skills.NL_MAPLE_WARRIOR:
				case Skills.SHADOWER_MAPLE_WARRIOR:
				case Skills.BUCCANEER_MAPLE_WARRIOR:
				case Skills.CORSAIR_MAPLE_WARRIOR:
					effects.add(PlayerStatusEffect.MAPLE_WARRIOR);
					break;
				case Skills.BOW_MASTER_SHARP_EYES:
				case Skills.XBOW_MASTER_SHARP_EYES:
					effects.add(PlayerStatusEffect.SHARP_EYES);
					break;
				case Skills.PHOENIX:
					effects.add(PlayerStatusEffect.SUMMON);
					break;
				case Skills.CLERIC_HOLY_SYMBOL:
				case Skills.GM_HOLY_SYMBOL:
					effects.add(PlayerStatusEffect.HOLY_SYMBOL);
					break;
				case Skills.SHADOW_STARS:
					effects.add(PlayerStatusEffect.SHADOW_STARS);
					break;
				case Skills.FP_INFINITY:
				case Skills.IL_INFINITY:
				case Skills.BISHOP_INFINITY:
					effects.add(PlayerStatusEffect.INFINITY);
					break;
				case Skills.HERO_POWER_STANCE:
				case Skills.PAGE_POWER_STANCE:
				case Skills.DARK_KNIGHT_POWER_STANCE:
					effects.add(PlayerStatusEffect.POWER_STANCE);
					break;
				case Skills.ECHO_OF_HERO:
					effects.add(PlayerStatusEffect.ECHO_OF_HERO);
					break;
				case Skills.FP_MANA_REFLECTION:
				case Skills.IL_MANA_REFLECTION:
				case Skills.BISHOP_MANA_REFLECTION:
					effects.add(PlayerStatusEffect.MANA_REFLECTION);
					break;
				case Skills.HOLY_SHIELD:
					effects.add(PlayerStatusEffect.HOLY_SHIELD);
					break;
				case Skills.BOW_PUPPET:
				case Skills.XBOW_PUPPET:
					effects.add(PlayerStatusEffect.PUPPET);
					break;

				case Skills.DISORDER:
				case Skills.THREATEN:
					monsterDiseases.add(MonsterStatusEffect.WATK);
					break;
				case Skills.DRAGON_ROAR:
				case Skills.ARROW_BOMB:
					monsterDiseases.add(MonsterStatusEffect.STUN);
					break;
				case Skills.NL_TAUNT:
				case Skills.SHADOWER_TAUNT:
					monsterDiseases.add(MonsterStatusEffect.TAUNT_1);
					monsterDiseases.add(MonsterStatusEffect.TAUNT_2);
					break;
				case Skills.COLD_BEAM:
				case Skills.IL_ELEMENT_COMPOSITION:
				case Skills.XBOW_BLIZZARD:
					monsterDiseases.add(MonsterStatusEffect.FREEZE);
					isFreeze = true;
					break;
				case Skills.HAMSTRING:
					effects.add(PlayerStatusEffect.HAMSTRING);
					//intentional fallthrough to speed mob debuff
				case Skills.FP_SLOW:
				case Skills.IL_SLOW:
					monsterDiseases.add(MonsterStatusEffect.SPEED);
					break;
				case Skills.SILVER_HAWK:
				case Skills.GOLDEN_EAGLE:
					effects.add(PlayerStatusEffect.SUMMON);
					monsterDiseases.add(MonsterStatusEffect.STUN);
					break;
				case Skills.ELQUINES:
				case Skills.FROSTPREY:
					effects.add(PlayerStatusEffect.SUMMON);
					monsterDiseases.add(MonsterStatusEffect.FREEZE);
					break;
				case Skills.BLIND:
					effects.add(PlayerStatusEffect.BLIND);
					monsterDiseases.add(MonsterStatusEffect.ACC);
					break;
			}
		}
	}

	protected void setY(int y) {
		if (y != 0) {
			this.y = y;
			switch (getDataId()) {
				case Skills.SPEARMAN_HYPER_BODY:
				case Skills.GM_HYPER_BODY:
					effects.add(PlayerStatusEffect.HYPER_BODY_MP);
					break;
				case Skills.DASH:
					effects.add(PlayerStatusEffect.DASH_JUMP);
					break;

				case Skills.DISORDER:
				case Skills.THREATEN:
					monsterDiseases.add(MonsterStatusEffect.WDEF);
					break;
			}
		}
	}

	protected void setZ(int z) {
		this.z = z;
	}

	protected void setDamage(short damage) {
		this.damage = damage;
	}

	protected void setLt(short x, short y) {
		this.lt = new Point(x, y);
	}

	protected void setRb(short x, short y) {
		this.rb = new Point(x, y);
	}

	protected void setMobCount(byte count) {
		this.mobCount = count;
	}

	protected void setProp(short prop) {
		this.prop = prop;
	}

	protected void setMastery(byte mastery) {
		this.mastery = mastery;
	}

	protected void setCooltime(short duration) {
		this.cooltime = duration;
	}

	protected void setRange(short distance) {
		this.range = distance;
	}

	protected void setAttackCount(byte count) {
		this.attackCount = count;
	}

	protected void setBulletCount(byte count) {
		this.bulletCount = count;
	}

	protected void setItemConsume(int itemCon) {
		this.itemCon = itemCon;
	}

	protected void setItemConsumeCount(byte count) {
		this.itemConCount = count;
	}

	protected void setBulletConsume(short bulletCon) {
		this.bulletCon = bulletCon;
	}

	protected void setMoneyConsume(short moneyCon) {
		this.moneyCon = moneyCon;
	}

	public void setHpRecoverRate(short hp) {
		this.hp = hp;
	}

	public void setMpRecoverRate(short mp) {
		this.mp = mp;
	}

	@Override
	public int getDuration() {
		//freeze skills have weird times...
		if (isFreeze)
			return super.getDuration() * 2;
		return super.getDuration();
	}

	public short getMpConsume() {
		return mpCon;
	}

	public short getHpConsume() {
		return hpCon;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public short getDamage() {
		return damage;
	}

	@Override
	public Rectangle getBoundingBox(Point posFrom, boolean facingLeft) {
		int ltx, lty, rbx, rby;
		if (facingLeft) {
			ltx = lt.x + posFrom.x;
			rbx = rb.x + posFrom.x;
		} else {
			ltx = -rb.x + posFrom.x;
			rbx = -lt.x + posFrom.x;
		}
		lty = lt.y + posFrom.y;
		rby = rb.y + posFrom.y;
		Rectangle bounds = new Rectangle(ltx, lty, rbx - ltx, rby - lty);
		return bounds;
	}

	public byte getMobCount() {
		return mobCount;
	}

	public short getProp() {
		return prop;
	}

	@Override
	public boolean makeChanceResult() {
		return Rng.getGenerator().nextInt(100) < prop;
	}

	public byte getMastery() {
		return mastery;
	}

	public short getCooltime() {
		return cooltime;
	}

	public short getRange() {
		return range;
	}

	public byte getAttackCount() {
		return attackCount;
	}

	public byte getBulletCount() {
		return bulletCount;
	}

	public int getItemConsume() {
		return itemCon;
	}

	public byte getItemConsumeCount() {
		return itemConCount;
	}

	public short getBulletConsume() {
		return bulletCon;
	}

	public short getMoneyConsume() {
		return moneyCon;
	}

	public short getHpRecoverRate() {
		return hp;
	}

	public short getMpRecoverRate() {
		return mp;
	}

	@Override
	public int hashCode() {
		//set the high bit to differentiate between skill and item effects that
		//have the same data id.
		//e.g. for use in hash maps that combine any kind of StatEffectsData.
		return getDataId() | 0x80000000;
	}

	@Override
	public Set<MonsterStatusEffect> getMonsterEffects() {
		return monsterDiseases;
	}

	@Override
	public byte getLevel() {
		return level;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}
}
