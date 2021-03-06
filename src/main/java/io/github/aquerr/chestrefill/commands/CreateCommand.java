package io.github.aquerr.chestrefill.commands;

import io.github.aquerr.chestrefill.SelectionMode;
import io.github.aquerr.chestrefill.ChestRefill;
import io.github.aquerr.chestrefill.PluginInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Aquerr on 2018-02-12.
 */
public class CreateCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException
    {
        Optional<String> optionalName = context.getOne(Text.of("箱子名称"));

        if (source instanceof Player)
        {
            Player player = (Player)source;

            if (ChestRefill.PlayersSelectionMode.containsKey(player.getUniqueId()))
            {
                if (SelectionMode.CREATE != ChestRefill.PlayersSelectionMode.get(player.getUniqueId()))
                {
                    optionalName.ifPresent(s -> ChestRefill.PlayerChestName.put(player.getUniqueId(), s));
                    ChestRefill.PlayersSelectionMode.replace(player.getUniqueId(), SelectionMode.CREATE);
                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "打开箱子创造模式"));
                }
                else
                {
                    ChestRefill.PlayerChestName.remove(player.getUniqueId());

                    ChestRefill.PlayersSelectionMode.remove(player.getUniqueId());
                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "关闭箱子创造模式"));
                }
            }
            else
            {
                optionalName.ifPresent(s -> ChestRefill.PlayerChestName.put(player.getUniqueId(), s));
                ChestRefill.PlayersSelectionMode.put(player.getUniqueId(), SelectionMode.CREATE);
                player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "打开箱子创造模式"));
            }
        }

        return CommandResult.success();
    }
}
