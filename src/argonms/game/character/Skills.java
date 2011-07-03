/*
 * ArgonMS MapleStory server emulator written in Java
 * Copyright (C) 2011  GoldenKevin
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

package argonms.game.character;

/**
 * A class of constants for player skills.
 * @author GoldenKevin
 */
public final class Skills {
	public static final int
		RECOVERY = 1001,
		MONSTER_RIDING = 1004,
		ECHO_OF_HERO = 1005,

		IMPROVED_MAXHP_INCREASE = 1000001,
		IRON_BODY = 1001003,

		CRUSADER_SWORD_MASTERY = 1100000,
		AXE_MASTERY = 1100001,
		CRUSADER_SWORD_BOOSTER = 1101004,
		AXE_BOOSTER = 1101005,
		RAGE = 1101006,
		FIGHTER_POWER_GUARD = 1101007,

		COMBO = 1111002,
		SWORD_COMA = 1111005,
		AXE_COMA = 1111006,
		SHOUT = 1111008,

		HERO_MAPLE_WARRIOR = 1121000,
		HERO_MONSTER_MAGNET = 1121001,
		HERO_POWER_STANCE = 1121002,
		ENRAGE = 1121010,

		PAGE_SWORD_MASTERY = 1200000,
		BW_MASTERY = 1200001,
		PAGE_SWORD_BOOSTER = 1201004,
		BW_BOOSTER = 1201005,
		THREATEN = 1201006,
		PAGE_POWER_GUARD = 1201007,

		CHARGED_BLOW = 1211002,
		SWORD_FIRE_CHARGE = 1211003,
		BW_FLAME_CHARGE = 1211004,
		SWORD_ICE_CHARGE = 1211005,
		BW_BLIZZARD_CHARGE = 1211006,
		SWORD_THUNDER_CHARGE = 1211007,
		BW_LIGHTNING_CHARGE = 1211008,

		PALADIN_MAPLE_WARRIOR = 1221000,
		PALADIN_MONSTER_MAGNET = 1221001,
		PAGE_POWER_STANCE = 1221002,
		SWORD_HOLY_CHARGE = 1221003,
		BW_DIVINE_CHARGE = 1221004,
		HEAVENS_HAMMER = 1221011,

		SPEAR_MASTERY = 1300000,
		POLE_ARM_MASTERY = 1300001,
		SPEAR_BOOSTER = 1301004,
		POLE_ARM_BOOSTER = 1301005,
		IRON_WILL = 1301006,
		SPEARMAN_HYPER_BODY = 1301007,

		DRAGON_ROAR = 1311006,
		DRAGON_BLOOD = 1311008,

		DARK_KNIGHT_MAPLE_WARRIOR = 1321000,
		DARK_KNIGHT_MONSTER_MAGNET = 1321001,
		DARK_KNIGHT_POWER_STANCE = 1321002,
		BEHOLDER = 1321007,

		IMPROVED_MAXMP_INCREASE = 2000001,
		MAGIC_GUARD = 2001002,
		MAGIC_ARMOR = 2001003,

		FP_MP_EATER = 2100000,
		FP_MEDITATION = 2101001,
		FP_SLOW = 2101003,
		POISON_BREATH = 2101005,

		EXPLOSION = 2111002,
		POISON_MIST = 2111003,
		FP_SEAL = 2111004,
		FP_SPELL_BOOSTER = 2111005,
		FP_ELEMENT_COMPOSITION = 2111006,

		FP_MAPLE_WARRIOR = 2121000,
		FP_BIG_BANG = 2121001,
		FP_MANA_REFLECTION = 2121002,
		FP_INFINITY = 2121004,
		ELQUINES = 2121005,
		PARALYZE = 2121006,

		IL_MP_EATER = 2200000,
		IL_MEDITATION = 2201001,
		IL_SLOW = 2201003,
		COLD_BEAM = 2201004,

		ICE_STRIKE = 2211002,
		IL_SEAL = 2211004,
		IL_SPELL_BOOSTER = 2211005,
		IL_ELEMENT_COMPOSITION = 2211006,

		IL_MAPLE_WARRIOR = 2221000,
		IL_BIG_BANG = 2221001,
		IL_MANA_REFLECTION = 2221002,
		IL_INFINITY = 2221004,
		IFRIT = 2221005,
		IL_BLIZZARD = 2221007,

		CLERIC_MP_EATER = 2300000,
		HEAL = 2301002,
		INVINCIBLE = 2301003,
		CLERIC_BLESS = 2301004,

		MYSTIC_DOOR = 2311002,
		CLERIC_HOLY_SYMBOL = 2311003,
		DOOM = 2311005,
		SUMMON_DRAGON = 2311006,

		BISHOP_MAPLE_WARRIOR = 2321000,
		BISHOP_BIG_BANG = 2321001,
		BISHOP_MANA_REFLECTION = 2321002,
		BISHOP_INFINITY = 2321004,
		HOLY_SHIELD = 2321005,
		BAHAMUT = 2321003,

		FOCUS = 3001003,

		BOW_MASTERY = 3100000,
		BOW_BOOSTER = 3101002,
		BOW_SOUL_ARROW = 3101004,
		ARROW_BOMB = 3101005,

		BOW_PUPPET = 3111002,
		ARROW_RAIN = 3111004,
		SILVER_HAWK = 3111005,

		BOW_MASTER_MAPLE_WARRIOR = 3121000,
		BOW_MASTER_SHARP_EYES = 3121002,
		HURRICANE = 3121004,
		PHOENIX = 3121006,
		HAMSTRING = 3121007,
		CONCENTRATE = 3121008,

		XBOW_MASTERY = 3200000,
		XBOW_BOOSTER = 3201002,
		XBOW_SOUL_ARROW = 3201004,

		XBOW_PUPPET = 3211002,
		XBOW_BLIZZARD = 3211003,
		ARROW_ERUPTION = 3211004,
		GOLDEN_EAGLE = 3211005,

		XBOW_MASTER_MAPLE_WARRIOR = 3221000,
		PIERCING_ARROW = 3221001,
		XBOW_MASTER_SHARP_EYES = 3221002,
		FROSTPREY = 3221005,
		BLIND = 3221006,
		SNIPE = 3221007,

		DISORDER = 4001002,
		DARK_SIGHT = 4001003,
		DOUBLE_STAB = 4001334,

		CLAW_MASTERY = 4100000,
		CLAW_BOOSTER = 4101003,
		SIN_HASTE = 4101004,
		DRAIN = 4101005,

		MESO_UP = 4111001,
		SHADOW_PARTNER = 4111002,
		SHADOW_WEB = 4111003,

		NL_NINJA_AMBUSH = 4121004,
		VENOMOUS_STAR = 4120005,
		NL_MAPLE_WARRIOR = 4121000,
		NL_TAUNT = 4121003,
		SHADOW_STARS = 4121006,

		DAGGER_MASTERY = 4200000,
		DAGGER_BOOSTER = 4201002,
		DIT_HASTE = 4201003,
		SAVAGE_BLOW = 4201005,

		CHAKRA = 4211001,
		ASSAULTER = 4211002,
		PICK_POCKET = 4211003,
		BAND_OF_THIEVES = 4211004,
		MESO_GUARD = 4211005,
		MESO_EXPLOSION = 4211006,

		VENOMOUS_STAB = 4220005,
		SHADOWER_MAPLE_WARRIOR = 4221000,
		SHADOWER_TAUNT = 4221003,
		SHADOWER_NINJA_AMBUSH = 4221004,
		SMOKESCREEN = 4221006,
		BOOMERANG_STEP = 4221007,

		DASH = 5001005,

		IMPROVE_MAXHP = 5100000,
		KNUCKLER_MASTERY = 5100001,
		BACKSPIN_BLOW = 5101002,
		DOUBLE_UPPERCUT = 5101003,
		CORKSCREW_BLOW = 5101004,
		KNUCKLER_BOOSTER = 5101006,

		ENERGY_CHARGE = 5110001,
		ENERGY_DRAIN = 5111004,

		BUCCANEER_MAPLE_WARRIOR = 5121000,
		ENERGY_ORB = 5121002,
		DEMOLITION = 5121004,
		SNATCH = 5121005,
		BARRAGE = 5121007,
		SPEED_INFUSION = 5121009,

		GUN_MASTERY = 5200000,
		GRENADE = 5201002,
		GUN_BOOSTER = 5201003,
		BLANK_SHOT = 5201004,

		OCTOPUS = 5211001,
		GAVIOTA = 5211002,
		ICE_SPLITTER = 5211005,

		WRATH_OF_THE_OCTOPI = 5220002,
		CORSAIR_MAPLE_WARRIOR = 5221000,
		RAPID_FIRE = 5221004,
		BATTLE_SHIP = 5221006,
		HYPNOTIZE = 5221009,

		GM_HASTE = 9101001,
		GM_HOLY_SYMBOL = 9101002,
		GM_BLESS = 9101003,
		HIDE = 9101004,
		GM_HYPER_BODY = 9101008
	;

	private Skills() {
		//uninstantiable...
	}
}