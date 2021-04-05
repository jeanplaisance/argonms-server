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

package argonms.game.field.entity;

import argonms.common.character.PlayerStatusEffect;
import argonms.common.character.inventory.Inventory;
import argonms.common.character.inventory.InventorySlot;
import argonms.common.character.inventory.InventoryTools;
import argonms.common.util.Rng;
import argonms.common.field.MonsterStatusEffect;
import argonms.common.loading.StatusEffectsData;
import argonms.common.loading.StatusEffectsData.MonsterStatusEffectsData;
import argonms.common.net.external.ClientSendOps;
import argonms.common.net.external.ClientSession;
import argonms.common.net.external.CommonPackets;
import argonms.common.util.Scheduler;
import argonms.common.util.collections.Pair;
import argonms.common.util.output.LittleEndianByteArrayWriter;
import argonms.game.GameServer;
import argonms.game.character.GameCharacter;
import argonms.game.character.PartyList;
import argonms.game.character.StatusEffectTools;
import argonms.game.character.inventory.ItemTools;
import argonms.game.field.AbstractEntity;
import argonms.game.field.Element;
import argonms.game.field.GameMap;
import argonms.game.field.MonsterStatusEffectTools;
import argonms.game.field.MonsterStatusEffectValues;
import argonms.game.loading.mob.MobDataLoader;
import argonms.game.loading.mob.MobStats;
import argonms.game.loading.mob.Skill;
import argonms.game.loading.skill.MobSkillEffectsData;
import argonms.game.net.external.GamePackets;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author GoldenKevin
 */
public class Mob extends AbstractEntity {
	public static final byte
		DESTROY_ANIMATION_NONE = 0,
		DESTROY_ANIMATION_NORMAL = 1,
		DESTROY_ANIMATION_EXPLODE = 2
	;

	public static final byte
		CONTROL_STATUS_NORMAL = 1,
		CONTROL_STATUS_NONE = 5
	;

	private final MobStats stats;
	private final GameMap map;
	private final AtomicInteger remHp;
	private final AtomicInteger remMp;
	private final Queue<MobDeathListener> subscribers;
	private final WeakHashMap<GameCharacter, PlayerAttacker> playerDamages;
	private final WeakHashMap<PartyList, PartyAttacker> partyDamages;
	private final Lock damagesReadLock, damagesWriteLock;
	private volatile GameCharacter controller;
	private volatile boolean aggroAware, hasAggro;
	private volatile ScheduledFuture<?> removeAfter;
	private final ConcurrentMap<MonsterStatusEffect, MonsterStatusEffectValues> activeEffects;
	private final ConcurrentMap<Short, ScheduledFuture<?>> skillFutures;
	private final ConcurrentMap<Integer, ScheduledFuture<?>> diseaseFutures;
	private final ConcurrentMap<Short, Long> skillsUsed;
	private final AtomicInteger spawnedSummons;
	private volatile byte spawnEffect, deathEffect;
	private volatile ScheduledFuture<?> poisonTask;
	private final ConcurrentLinkedQueue<Long> venomExpires;
	private volatile ScheduledFuture<?> venomDecrementTask;
	private volatile int venomOwner;

	public Mob(MobStats stats, GameMap map, byte stance) {
		this.stats = stats;
		this.map = map;
		this.remHp = new AtomicInteger(stats.getMaxHp());
		this.remMp = new AtomicInteger(stats.getMaxMp());
		this.subscribers = new ConcurrentLinkedQueue<MobDeathListener>();
		this.playerDamages = new WeakHashMap<GameCharacter, PlayerAttacker>();
		this.partyDamages = new WeakHashMap<PartyList, PartyAttacker>();
		ReadWriteLock lock = new ReentrantReadWriteLock();
		this.damagesReadLock = lock.readLock();
		this.damagesWriteLock = lock.writeLock();
		this.activeEffects = new ConcurrentSkipListMap<MonsterStatusEffect, MonsterStatusEffectValues>();
		this.skillFutures = new ConcurrentHashMap<Short, ScheduledFuture<?>>();
		this.diseaseFutures = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
		this.skillsUsed = new ConcurrentHashMap<Short, Long>();
		this.spawnedSummons = new AtomicInteger(0);
		this.deathEffect = stats.getDeathAnimation();
		this.venomExpires = new ConcurrentLinkedQueue<Long>();
		setStance(stance);
	}

	public Mob(MobStats stats, GameMap map) {
		this(stats, map, (byte) 5);
	}

	public int getDataId() {
		return stats.getMobId();
	}

	public GameMap getMap() {
		return map;
	}

	public boolean isMobile() {
		//TODO: fix for MCDB
		return stats.getDelays().containsKey("move") || stats.getDelays().containsKey("fly");
	}

	public boolean isBoss() {
		return stats.isBoss();
	}

	private List<ItemDrop> getDrops() {
		List<InventorySlot> items = stats.getItemsToDrop();
		List<ItemDrop> combined = new ArrayList<ItemDrop>(items.size() + 1);
		for (InventorySlot item : items)
			combined.add(new ItemDrop(item));
		int dropMesos = stats.getMesosToDrop();
		if (dropMesos != 0){
			combined.add(new ItemDrop(dropMesos));
			combined = getAdditionalMesoDrops(combined, dropMesos, 120000, 1);
		}
		Collections.shuffle(combined);
		return combined;
	}

	private List<ItemDrop> getAdditionalMesoDrops(List<ItemDrop> combined, int dropMesos, int probability, int bonus){
		if(bonus == 32)
			return combined;
		Random generator = Rng.getGenerator();
		if (generator.nextInt(1000000) < probability) {
			for(int i = 0; i<bonus; i++)
				combined.add(new ItemDrop((int)(dropMesos * (100 + generator.nextInt(30) - 10)/100)));
			return getAdditionalMesoDrops(combined, dropMesos, (int)(probability*0.12), bonus*2);
		} else {
			return combined;
		}
	}

	private void schedulePostDeathAnimationTasks(final int owner, final byte pickupAllow) {
		Scheduler.getInstance().runAfterDelay(new Runnable() {
			@Override
			public void run() {
				//drops
				map.drop(getDrops(), Mob.this, pickupAllow, owner);

				//revives
				for (Integer mobId : stats.getSummons()) {
					Mob spawn = new Mob(MobDataLoader.getInstance().getMobStats(mobId.intValue()), map);
					spawn.setPosition(new Point(getPosition()));
					map.spawnMonster(spawn);
				}
			}
		}, getAnimationTime("die1"));
	}

	private Pair<Attacker, GameCharacter> giveExp(GameCharacter killer) {
		Attacker highestDamageAttacker = null;
		GameCharacter highestDamageIndividual = null;
		long highestDamage = 0;
		long highestIndividualDamage = 0;

		damagesReadLock.lock();
		try {
			List<Attacker> allDamages = new ArrayList<Attacker>(playerDamages.size() + partyDamages.size());
			allDamages.addAll(playerDamages.values());
			allDamages.addAll(partyDamages.values());

			for (Attacker pd : allDamages) {
				long damage = pd.getHighestDamage();
				if (damage > highestIndividualDamage) {
					highestDamageIndividual = pd.getHighestDamageAttacker();
					highestIndividualDamage = damage;
				}
				damage = pd.totalDamage();
				if (damage > highestDamage) {
					highestDamageAttacker = pd;
					highestDamage = damage;
				}

				long exp = stats.getExp() * ((8 * damage / stats.getMaxHp()) + (pd.attackersInclude(killer) ? 2 : 0)) / 10;
				pd.distributeExp(exp, killer);
			}
		} finally {
			damagesReadLock.unlock();
		}
		return new Pair<Attacker, GameCharacter>(highestDamageAttacker, highestDamageIndividual);
	}

	public void died(GameCharacter killer) {
		Set<StatusEffectsData> sources = new HashSet<StatusEffectsData>();
		//TODO: race condition for getAllEffects() if skill expires while
		//this loop is running
		for (Map.Entry<MonsterStatusEffect, MonsterStatusEffectValues> effect : getAllEffects().entrySet()) {
			removeFromActiveEffects(effect.getKey());
			MonsterStatusEffectTools.dispelEffect(this, effect.getKey(), effect.getValue());
			sources.add(effect.getValue().getEffectsData());
		}
		for (StatusEffectsData e : sources)
			removeCancelEffectTask(e);
		if (removeAfter != null)
			removeAfter.cancel(false);
		int deathBuff = stats.getBuffToGive();
		if (deathBuff > 0) {
			ItemTools.useItem(killer, deathBuff);
			killer.getClient().getSession().send(GamePackets.writeSelfVisualEffect(StatusEffectTools.MOB_BUFF, deathBuff, (byte) 1, (byte) -1));
			map.sendToAll(GamePackets.writeBuffMapVisualEffect(killer, StatusEffectTools.MOB_BUFF, deathBuff, (byte) 1, (byte) -1), killer);
		}
		for (Integer itemId : stats.getItemsToTake()) {
			Inventory.InventoryType type = InventoryTools.getCategory(itemId);
			int quantity = InventoryTools.getAmountOfItem(killer.getInventory(type), itemId);
			if (type == Inventory.InventoryType.EQUIP)
				quantity += InventoryTools.getAmountOfItem(killer.getInventory(Inventory.InventoryType.EQUIPPED), itemId);
			if (quantity > 0) {
				Inventory inv = killer.getInventory(type);
				InventoryTools.UpdatedSlots changedSlots = InventoryTools.removeFromInventory(killer, itemId, quantity);
				ClientSession<?> ses = killer.getClient().getSession();
				short pos;
				for (Short s : changedSlots.modifiedSlots) {
					pos = s.shortValue();
					ses.send(CommonPackets.writeInventoryUpdateSlotQuantity(type, pos, inv.get(pos)));
				}
				for (Short s : changedSlots.addedOrRemovedSlots) {
					pos = s.shortValue();
					ses.send(CommonPackets.writeInventoryClearSlot(type, pos));
				}
				killer.itemCountChanged(itemId);
				ses.send(GamePackets.writeShowItemGainFromQuest(itemId, -quantity));
			}
		}
		Pair<Attacker, GameCharacter> highestDamage = giveExp(killer);
		for (MobDeathListener hook : subscribers)
			hook.monsterKilled(highestDamage.right, killer);
		schedulePostDeathAnimationTasks(highestDamage.left.getId(), highestDamage.left.getDropPickUpAllow());
	}

	public void addListener(MobDeathListener subscriber) {
		subscribers.offer(subscriber);
	}

	public void fireDeathEventNoRewards() {
		for (ScheduledFuture<?> cancelTask : skillFutures.values())
			cancelTask.cancel(false);
		for (ScheduledFuture<?> cancelTask : diseaseFutures.values())
			cancelTask.cancel(false);
		if (removeAfter != null)
			removeAfter.cancel(false);
		for (MobDeathListener subscriber : subscribers)
			subscriber.monsterKilled(null, null);
	}

	public byte getControlStatus() {
		return controller != null ? CONTROL_STATUS_NORMAL : CONTROL_STATUS_NONE;
	}

	public GameCharacter getController() {
		return controller;
	}

	public void setController(GameCharacter newController) {
		this.controller = newController;
	}

	public boolean isFirstAttack() {
		return stats.isFirstAttack();
	}

	public int getHp() {
		return remHp.get();
	}

	public int getMaxHp() {
		return stats.getMaxHp();
	}

	public int getMp() {
		return remMp.get();
	}

	public int getMaxMp() {
		return stats.getMaxMp();
	}

	public short getLevel() {
		return stats.getLevel();
	}

	public int getRemoveAfter() {
		return stats.getRemoveAfter();
	}

	public void setSelfRemoveFuture(ScheduledFuture<?> future) {
		removeAfter = future;
	}

	public byte getDropItemPeriod() {
		return stats.getDropItemPeriod();
	}

	/**
	 * Atomically adds a value to an AtomicInteger and clip the resulting value
	 * to within a certain range. This is equivalent to
	 * <pre>
	 *   int newValue = i.addAndGet(delta);
	 *   if (i.get() &lt; min)
	 *       i.set(min);
	 *   if (i.get() &gt; max)
	 *       i.set(max);
	 *   return newValue;</pre>
	 * except that the action is performed atomically.
	 * @param i the AtomicInteger instance
	 * @param delta the value to add
	 * @param min inclusive
	 * @param max inclusive
	 * @return the value that <tt>i</tt> would've held if it was unclamped.
	 */
	private int clampedAdd(AtomicInteger i, int delta, int min, int max) {
		//copied from AtomicInteger.addAndGet(int). only difference is that we
		//set the value to the clamped next (we still return the unclamped next)
		while (true) {
			int current = i.get();
			int next = current + delta;
			if (i.compareAndSet(current, Math.min(Math.max(next, min), max)))
				return next;
		}
	}

	/**
	 * Atomically sets a value if the AtomicInteger is within a range. This is
	 * equivalent to
	 * <pre>
	 *   if (i.get() &gt;= min &amp;&amp; i.get() &lt;= max) {
	 *       i.set(update);
	 *       return true;
	 *   }
	 *   return false;</pre>
	 * except that the action is performed atomically.
	 * @param i the AtomicInteger instance
	 * @param update the new value
	 * @param min inclusive
	 * @param max inclusive
	 * @return <tt>true</tt> if <tt>i</tt> was in bounds, <tt>false</tt> otherwise
	 */
	private boolean setIfInBounds(AtomicInteger i, int update, int min, int max) {
		while (true) {
			int current = i.get();
			if (current >= min && current <= max) {
				if (i.compareAndSet(current, update))
					return true;
			} else {
				return false;
			}
		}
	}

	public void hurt(GameCharacter p, int damage) {
		if (stats.isInvincible())
			return;

		int overkill = -clampedAdd(remHp, -damage, 0, Integer.MAX_VALUE);
		if (overkill > 0)
			damage -= overkill;

		if (p != null) {
			damagesWriteLock.lock();
			try {
				boolean firstHitByAttacker = false;
				if (p.getParty() != null) {
					PartyAttacker pd = partyDamages.get(p.getParty());
					if (pd == null) {
						pd = new PartyAttacker(p.getParty());
						partyDamages.put(p.getParty(), pd);
						firstHitByAttacker = true;
					}
					pd.addDamage(p, damage);
				} else {
					PlayerAttacker pd = playerDamages.get(p);
					if (pd == null) {
						pd = new PlayerAttacker(p);
						playerDamages.put(p, pd);
						firstHitByAttacker = true;
					}
					pd.addDamage(p, damage);
				}
				if (firstHitByAttacker)
					subscribers.offer(p.getMobDeathListener(getDataId()));
			} finally {
				damagesWriteLock.unlock();
			}
		}

		//TODO: add friendly mob damage stuffs too (after stats.isBoss check)
		if (stats.getHpTagColor() > 0) //boss
			map.sendToAll(writeShowBossHp(stats, remHp.get()));
		else if (stats.isBoss()) //minibosses
			map.sendToAll(writeShowMobHp(getId(), (byte) (remHp.get() * 100 / stats.getMaxHp())));
		else if (p != null)
			p.getClient().getSession().send(writeShowMobHp(getId(), (byte) (remHp.get() * 100 / stats.getMaxHp())));

		if (stats.getSelfDestructHp() != 0)
			if (remHp.get() == 0) //don't explode if we damaged it enough to have killed it without self destruct
				deathEffect = DESTROY_ANIMATION_NORMAL;
			else //set hp to 0 if hp is below threshold. by default, deathEffect is the mob's explosion animation
				setIfInBounds(remHp, 0, 1, stats.getSelfDestructHp());
	}

	public void loseMp(int loss) {
		this.remMp.addAndGet(-loss);
	}

	public List<Skill> getSkills() {
		return stats.getSkills();
	}

	public boolean canUseSkill(MobSkillEffectsData effect) {
		Long usedTime = skillsUsed.get(Short.valueOf((short) effect.getDataId()));
		long now = System.currentTimeMillis();
		if (usedTime != null && now - usedTime.longValue() <= effect.getCooltime() * 1000)
			return false;

		skillsUsed.put(Short.valueOf((short) effect.getDataId()), Long.valueOf(now));
		if ((remHp.get() * 100 / stats.getMaxHp()) > effect.getMaxPercentHp())
			return false;

		return true;
	}

	public boolean hasSkill(short skillId, byte skillLevel) {
		for (Skill s : stats.getSkills())
			if (s.getSkill() == skillId && s.getLevel() == skillLevel)
				return true;
		return false;
	}

	public byte getElementalResistance(Element elem) {
		if (activeEffects.containsKey(MonsterStatusEffect.DOOM))
			return Element.EFFECTIVENESS_NORMAL;
		return stats.getElementalResistance(elem);
	}

	public void setPoisonTask(ScheduledFuture<?> f) {
		poisonTask = f;
	}

	public ScheduledFuture<?> removePoisonTask() {
		ScheduledFuture<?> f = poisonTask;
		poisonTask = null;
		return f;
	}

	public int getVenomCount() {
		return venomExpires.size();
	}

	public void incrementVenom(int duration) {
		venomExpires.offer(Long.valueOf(System.currentTimeMillis() + duration));
	}

	public void decrementVenom() {
		venomExpires.poll();
	}

	public int nextVenomExpire() {
		return (int) (venomExpires.peek().longValue() - System.currentTimeMillis());
	}

	public void resetVenom() {
		venomExpires.clear();
	}

	public void setVenomDecrementTask(ScheduledFuture<?> f) {
		venomDecrementTask = f;
	}

	public ScheduledFuture<?> removeVenomDecrementTask() {
		ScheduledFuture<?> f = venomDecrementTask;
		venomDecrementTask = null;
		return f;
	}

	public void setVenomOwner(GameCharacter p) {
		venomOwner = p.getId();
	}

	public int removeVenomOwner() {
		int playerId = venomOwner;
		venomOwner = 0;
		return playerId;
	}

	public boolean canAcceptVenom(GameCharacter p) {
		return venomOwner == 0 || p.getId() == venomOwner;
	}

	public void addToActiveEffects(MonsterStatusEffect buff, MonsterStatusEffectValues value) {
		activeEffects.put(buff, value);
	}

	public void addCancelEffectTask(StatusEffectsData e, ScheduledFuture<?> cancelTask) {
		switch (e.getSourceType()) {
			case MOB_SKILL:
				skillFutures.put(Short.valueOf((short) e.getDataId()), cancelTask);
				break;
			case PLAYER_SKILL:
				diseaseFutures.put(Integer.valueOf(e.getDataId()), cancelTask);
				break;
		}
	}

	public Map<MonsterStatusEffect, MonsterStatusEffectValues> getAllEffects() {
		return activeEffects;
	}

	public MonsterStatusEffectValues getEffectValue(MonsterStatusEffect buff) {
		return activeEffects.get(buff);
	}

	public MonsterStatusEffectValues removeFromActiveEffects(MonsterStatusEffect e) {
		return activeEffects.remove(e);
	}

	public void removeCancelEffectTask(StatusEffectsData e) {
		ScheduledFuture<?> cancelTask;
		switch (e.getSourceType()) {
			case MOB_SKILL:
				cancelTask = skillFutures.remove(Short.valueOf((short) e.getDataId()));
				break;
			case PLAYER_SKILL:
				cancelTask = diseaseFutures.remove(Integer.valueOf(e.getDataId()));
				break;
			default:
				cancelTask = null;
				break;
		}
		if (cancelTask != null)
			cancelTask.cancel(false);
	}

	public boolean isEffectActive(MonsterStatusEffect b) {
		return activeEffects.containsKey(b);
	}

	public boolean isSkillActive(short skillid) {
		return skillFutures.containsKey(Short.valueOf(skillid));
	}

	public boolean isDebuffActive(int playerSkillId) {
		return diseaseFutures.containsKey(Integer.valueOf(playerSkillId));
	}

	public boolean areEffectsActive(MonsterStatusEffectsData e) {
		switch (e.getSourceType()) {
			case PLAYER_SKILL:
				return diseaseFutures.containsKey(Integer.valueOf(e.getDataId()));
			case MOB_SKILL:
				return skillFutures.containsKey(Short.valueOf((short) e.getDataId()));
			default:
				return false;
		}
	}

	public int getSpawnedSummons() {
		return spawnedSummons.get();
	}

	public void addToSpawnedSummons() {
		spawnedSummons.incrementAndGet();
	}

	public void setSpawnEffect(byte effect) {
		this.spawnEffect = effect;
	}

	public boolean wasAttackedBy(GameCharacter player) {
		damagesReadLock.lock();
		try {
			if (player.getParty() != null) {
				PartyAttacker pd = partyDamages.get(player.getParty());
				return pd != null && pd.attackersInclude(player);
			}
			return playerDamages.containsKey(player);
		} finally {
			damagesReadLock.unlock();
		}
	}

	public boolean controllerHasAggro() {
		return hasAggro;
	}

	public void setControllerHasAggro(boolean controllerHasAggro) {
		this.hasAggro = controllerHasAggro;
	}

	public boolean controllerKnowsAboutAggro() {
		return aggroAware;
	}

	public void setControllerKnowsAboutAggro(boolean aware) {
		this.aggroAware = aware;
	}

	public int getAnimationTime(String name) {
		Integer time = stats.getDelays().get(name);
		return time != null ? time.intValue() : 0;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.MONSTER;
	}

	@Override
	public boolean isAlive() {
		return remHp.get() > 0;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public byte[] getShowNewSpawnMessage() {
		return GamePackets.writeShowMonster(this, true, spawnEffect);
	}

	@Override
	public byte[] getShowExistingSpawnMessage() {
		return GamePackets.writeShowMonster(this, false, spawnEffect);
	}

	@Override
	public byte[] getDestructionMessage() {
		return GamePackets.writeRemoveMonster(this, deathEffect);
	}

	public interface MobDeathListener {
		public void monsterKilled(GameCharacter highestDamage, GameCharacter last);
	}

	private interface Attacker {
		public long totalDamage();
		public void distributeExp(long share, GameCharacter killer); //TODO: taunt, curse
		public void addDamage(GameCharacter c, int gain);
		public long getHighestDamage();
		public GameCharacter getHighestDamageAttacker();
		public boolean attackersInclude(GameCharacter p);
		public byte getDropPickUpAllow();
		public int getId();
	}

	private class PlayerAttacker implements Attacker {
		private final WeakReference<GameCharacter> player;
		private long damage;

		public PlayerAttacker(GameCharacter player) {
			this.player = new WeakReference<GameCharacter>(player);
		}

		@Override
		public long totalDamage() {
			return damage;
		}

		@Override
		public void distributeExp(long share, GameCharacter killer) {
			GameCharacter attacker = player.get();
			if (attacker == null || attacker.isClosed() || attacker.getMapId() != map.getDataId() || !attacker.isAlive())
				return;
			int hsRate = player.get().isEffectActive(PlayerStatusEffect.HOLY_SYMBOL) ?
					player.get().getEffectValue(PlayerStatusEffect.HOLY_SYMBOL).getModifier() : 0;
			share *= GameServer.getVariables().getExpRate();
			//share = share * getTauntEffect() / 100;
			share += share * hsRate / 100;
			player.get().gainExp((int) Math.min(share, Integer.MAX_VALUE), player.get() == killer, false);
		}

		@Override
		public void addDamage(GameCharacter c, int gain) {
			damage += gain;
		}

		@Override
		public long getHighestDamage() {
			return damage;
		}

		@Override
		public GameCharacter getHighestDamageAttacker() {
			return player.get();
		}

		@Override
		public boolean attackersInclude(GameCharacter p) {
			return player.get() == p;
		}

		@Override
		public byte getDropPickUpAllow() {
			return ItemDrop.PICKUP_ALLOW_OWNER;
		}

		@Override
		public int getId() {
			GameCharacter p = player.get();
			if (p == null)
				return 0;
			return p.getId();
		}
	}

	private class PartyAttacker implements Attacker {
		private WeakReference<PartyList> party;
		private final WeakHashMap<GameCharacter, Long> attackers;
		private short lowestAttackerLevel;
		private WeakReference<GameCharacter> highestDamageAttacker;

		public PartyAttacker(PartyList party) {
			this.party = new WeakReference<PartyList>(party);
			attackers = new WeakHashMap<GameCharacter, Long>();
			lowestAttackerLevel = 0xFF;
			highestDamageAttacker = new WeakReference<GameCharacter>(null);
		}

		@Override
		public long totalDamage() {
			long sum = 0;
			synchronized (attackers) {
				for (Long individualDamage : attackers.values())
					sum += individualDamage.longValue();
			}
			return sum;
		}

		@Override
		public void distributeExp(long share, GameCharacter killer) {
			synchronized (attackers) {
				PartyList attackerParty = party.get();
				if (attackerParty == null)
					return;
				short totalLevel = 0;
				int membersCount = 0;
				List<GameCharacter> splitExpMembers = new ArrayList<GameCharacter>();
				int hsRate = 0;
				attackerParty.lockRead();
				try {
					int minAttackerLevel = lowestAttackerLevel - 5;
					for (GameCharacter member : attackerParty.getLocalMembersInMap(map.getDataId())) {
						if (member.isClosed() || member.getMapId() != map.getDataId() || !member.isAlive())
							continue;
						short attackerLevel = member.getLevel();
						if (attackerLevel >= minAttackerLevel || attackerLevel >= (stats.getLevel() - 5) || attackers.containsKey(member)) {
							totalLevel += attackerLevel;
							splitExpMembers.add(member);
							//TODO: if more than one priest, only use highest? or use latest cast?
							hsRate = Math.max(member.isEffectActive(PlayerStatusEffect.HOLY_SYMBOL) ?
									member.getEffectValue(PlayerStatusEffect.HOLY_SYMBOL).getModifier() : 0, hsRate);
						}
						membersCount++; //I'm pretty sure party bonus is based on every member, not just for those who are getting EXP
					}
				} finally {
					attackerParty.unlockRead();
				}
				for (GameCharacter member : splitExpMembers) {
					long exp = share * ((8 * member.getLevel() / totalLevel) + (member == highestDamageAttacker.get() ? 2 : 0)) / 10;
					exp *= GameServer.getVariables().getExpRate();
					//exp = exp * getTauntEffect() / 100;
					if (membersCount > 1)
						exp += exp * 5 * membersCount / 100; //party bonus, 5% for each member
					exp += exp * hsRate / 100;
					member.gainExp((int) Math.min(exp, Integer.MAX_VALUE), member == killer, false);
				}
			}
		}

		@Override
		public void addDamage(GameCharacter c, int gain) {
			synchronized (attackers) {
				Long currentDamage = attackers.get(c);
				if (currentDamage != null)
					currentDamage = Long.valueOf(currentDamage.longValue() + gain);
				else
					currentDamage = Long.valueOf(gain);
				attackers.put(c, currentDamage);
				GameCharacter currentHighestDamageAttacker = highestDamageAttacker.get();
				if (currentHighestDamageAttacker == null || currentDamage.compareTo(attackers.get(currentHighestDamageAttacker)) > 0)
					highestDamageAttacker = new WeakReference<GameCharacter>(c);
				if (c.getLevel() < lowestAttackerLevel)
					lowestAttackerLevel = c.getLevel();
			}
		}

		@Override
		public long getHighestDamage() {
			Long damage = attackers.get(highestDamageAttacker.get());
			return damage != null ? damage.longValue() : 0;
		}

		@Override
		public GameCharacter getHighestDamageAttacker() {
			return highestDamageAttacker.get();
		}

		@Override
		public boolean attackersInclude(GameCharacter p) {
			return attackers.containsKey(p);
		}

		@Override
		public byte getDropPickUpAllow() {
			return ItemDrop.PICKUP_ALLOW_PARTY;
		}

		@Override
		public int getId() {
			PartyList p = party.get();
			if (p == null)
				return 0;
			return p.getId();
		}
	}

	private static byte[] writeShowMobHp(int mobid, byte percent) {
		LittleEndianByteArrayWriter lew = new LittleEndianByteArrayWriter();

		lew.writeShort(ClientSendOps.SHOW_MONSTER_HP);
		lew.writeInt(mobid);
		lew.writeByte(percent);

		return lew.getBytes();
	}

	private static byte[] writeShowBossHp(MobStats stats, int remHp) {
		LittleEndianByteArrayWriter lew = new LittleEndianByteArrayWriter();

		lew.writeShort(ClientSendOps.MAP_EFFECT);
		lew.writeByte((byte) 0x05);
		lew.writeInt(stats.getMobId());
		lew.writeInt(remHp);
		lew.writeInt(stats.getMaxHp());
		lew.writeByte(stats.getHpTagColor());
		lew.writeByte(stats.getHpTagBgColor());

		return lew.getBytes();
	}
}
