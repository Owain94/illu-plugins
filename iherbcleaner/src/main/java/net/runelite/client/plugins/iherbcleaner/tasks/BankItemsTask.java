package net.runelite.client.plugins.iherbcleaner.tasks;

import java.awt.Rectangle;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.iherbcleaner.Task;
import net.runelite.client.plugins.iherbcleaner.iHerbCleanerPlugin;
import static net.runelite.client.plugins.iherbcleaner.iHerbCleanerPlugin.startBot;
import static net.runelite.client.plugins.iherbcleaner.iHerbCleanerPlugin.status;
import net.runelite.client.plugins.iutils.ActionQueue;
import net.runelite.client.plugins.iutils.BankUtils;
import net.runelite.client.plugins.iutils.InventoryUtils;

@Slf4j
public class BankItemsTask extends Task
{

	@Inject
	ActionQueue action;

	@Inject
	InventoryUtils inventory;

	@Inject
	BankUtils bank;

	@Override
	public boolean validate()
	{
		return action.delayedActions.isEmpty() && !inventory.containsItem(config.herbID()) &&
			bank.isOpen();
	}

	@Override
	public String getTaskDescription()
	{
		return iHerbCleanerPlugin.status;
	}

	@Override
	public void onGameTick(GameTick event)
	{
		int sleep = 0;
		if (!inventory.isEmpty())
		{
			status = "Depositing items";
			bank.depositAll();
		}
		else
		{
			status = "Withdrawing items";
			if (bank.contains(config.herbID(), 1))
			{
				bank.withdrawAllItem(config.herbID());
			}
			else
			{
				status = "Out of herbs to clean, stopping";
				utils.sendGameMessage(status);
				startBot = false;
			}
		}
		log.info(status);
	}
}