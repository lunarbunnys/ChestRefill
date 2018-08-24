package io.github.aquerr.chestrefill.listeners;

import io.github.aquerr.chestrefill.ChestRefill;
import io.github.aquerr.chestrefill.PluginInfo;
import io.github.aquerr.chestrefill.entities.RefillableContainer;
import io.github.aquerr.chestrefill.managers.ContainerManager;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;

/**
 * Created by Aquerr on 2018-02-10.
 */

public class RightClickListener
{
    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event, @Root Player player)
    {
        if(ChestRefill.PlayersSelectionMode.containsKey(player.getUniqueId()))
        {
            if (event.getTargetBlock().getLocation().isPresent())
            {
                if(event.getTargetBlock().getLocation().get().getTileEntity().isPresent())
                {
                    TileEntity tileEntity = event.getTargetBlock().getLocation().get().getTileEntity().get();
                    if (tileEntity instanceof TileEntityCarrier)
                    {
                        RefillableContainer refillableContainer = RefillableContainer.fromTileEntity(tileEntity, player.getWorld().getUniqueId());
                        switch (ChestRefill.PlayersSelectionMode.get(player.getUniqueId()))
                        {
                            case CREATE:
                                if (!ContainerManager.getRefillableContainers().stream().anyMatch(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())))
                                {
                                    if(ChestRefill.PlayerChestName.containsKey(player.getUniqueId()))
                                        refillableContainer.setName(ChestRefill.PlayerChestName.get(player.getUniqueId()));

                                    boolean didSucceed = ContainerManager.addRefillableContainer(refillableContainer);
                                    if (didSucceed)
                                    {
                                        player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "成功创建了一个重装箱子!"));
                                    }
                                    else player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "- -什么东西出了错误..."));
                                }
                                else
                                {
                                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "这个箱子已经是一个重装箱子了!"));
                                }

                                //Turns off selection mode. It will be more safe to turn it off and let the player turn it on again.
                                ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());
                                break;

                            case REMOVE:

                                if (ContainerManager.getRefillableContainers().stream().anyMatch(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())))
                                {
                                    boolean didSucceed = ContainerManager.removeRefillableContainer(refillableContainer.getContainerLocation());

                                    if (didSucceed)
                                    {
                                        player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "成功删除一个重装箱子!"));
                                    }
                                    else player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "- -什么东西出了错误..."));
                                }
                                else
                                {
                                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "这不是一个重装箱子!"));
                                }

                                //Turn off selection mode. It will be more safe to turn it off and let the player turn it on again.
                                ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());
                                break;

                            case UPDATE:

                                if (ContainerManager.getRefillableContainers().stream().anyMatch(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())))
                                {
                                    boolean didSucceed = ContainerManager.updateRefillableContainer(refillableContainer);

                                    if (didSucceed)
                                    {
                                        player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "成功更新一个重装箱子!"));
                                    }
                                    else player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "- -什么东西出了错误..."));
                                }
                                else
                                {
                                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "这不是一个重装箱子!"));
                                }

                                //Turn off selection mode. It will be more safe to turn it off and let the player turn it on again.
                                ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());

                                break;

                            case TIME:
                                if (ContainerManager.getRefillableContainers().stream().anyMatch(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())))
                                {
                                    if (ChestRefill.ContainerTimeChangePlayer.containsKey(player.getUniqueId()))
                                    {
                                        int time = ChestRefill.ContainerTimeChangePlayer.get(player.getUniqueId());

                                        boolean didSucceed = ContainerManager.updateRefillingTime(refillableContainer.getContainerLocation(), time);

                                        if (didSucceed)
                                        {
                                            player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "成功更新这个箱子的重装时间!"));
                                        }
                                        else player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "- -什么东西出了错误..."));
                                    }
                                    else
                                    {
                                        RefillableContainer chestToView = ContainerManager.getRefillableContainers().stream().filter(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())).findFirst().get();
                                        player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "这个箱子每 ", TextColors.GREEN, chestToView.getRestoreTime(), TextColors.YELLOW, " 秒重装一次"));
                                    }
                                }
                                else
                                {
                                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "这不是一个重装箱子!"));
                                }

                                //Turn off selection mode. It will be more safe to turn it off and let the player turn it on again.
                                ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());
                                ChestRefill.ContainerTimeChangePlayer.remove(player.getUniqueId());

                                break;

                            case SETNAME:
                                if (ContainerManager.getRefillableContainers().stream().anyMatch(x->x.getContainerLocation().equals(refillableContainer.getContainerLocation())))
                                {
                                    boolean didSucceed = ContainerManager.renameRefillableContainer(refillableContainer.getContainerLocation(), ChestRefill.PlayerChestName.get(player.getUniqueId()));

                                    if (didSucceed)
                                    {
                                        player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "成功更新一个重装箱子!"));
                                    }
                                    else player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "- -什么东西出了错误..."));
                                }
                                else
                                {
                                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "这不是一个重装箱子!"));
                                }

                                //Turn off selection mode. It will be more safe to turn it off and let the player turn it on again.
                                ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());
                                break;
                        }
                    }
                }
            }
        }
    }
}
