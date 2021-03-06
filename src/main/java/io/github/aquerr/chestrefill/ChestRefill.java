package io.github.aquerr.chestrefill;

import io.github.aquerr.chestrefill.commands.*;
import io.github.aquerr.chestrefill.commands.arguments.ContainerNameArgument;
import io.github.aquerr.chestrefill.listeners.ContainerBreakListener;
import io.github.aquerr.chestrefill.listeners.PlayerJoinListener;
import io.github.aquerr.chestrefill.listeners.RightClickListener;
import io.github.aquerr.chestrefill.managers.ContainerManager;
import io.github.aquerr.chestrefill.version.VersionChecker;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.*;


/**
 * Created by Aquerr on 2018-02-09.
 */

@Plugin(id = PluginInfo.Id, name = PluginInfo.Name, version = PluginInfo.Version, description = PluginInfo.Description, authors = PluginInfo.Authors, url = PluginInfo.Url)
public class ChestRefill
{
    public static Map<List<String>, CommandSpec> Subcommands = new HashMap<>();

    public static Map<UUID, SelectionMode> PlayersSelectionMode = new HashMap<>();
    public static Map<UUID, String> PlayerChestName = new HashMap<>();
    public static Map<UUID, Integer> ContainerTimeChangePlayer = new HashMap<>();

    private static ChestRefill chestRefill;
    public static ChestRefill getChestRefill() {return chestRefill;}

    @Inject
    private Logger _logger;
    public Logger getLogger() {return _logger;}

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path _configDir;
    public Path getConfigDir() {return _configDir;}

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {
        chestRefill = this;

        ContainerManager.setupContainerManager(_configDir);

        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Chest Refill 正在加载中... :D"));
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "初始化指令..."));

        initCommands();

        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "初始化监听器..."));

        initListeners();

        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Chest Refill 已加载!"));

        if (VersionChecker.isLatest(PluginInfo.Version))
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "你正在使用最新版本!"));
        }
        else
        {
            Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "一个新的版本 ", TextColors.YELLOW, PluginInfo.Name, TextColors.RED, " 可用!"));
        }
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN,"汉化：月光兔<银河系气功协会>"));

    }

    @Listener
    public void onGameLoad(GameLoadCompleteEvent event)
    {
        //Start refilling chests that were created on the server before
        ContainerManager.restoreRefilling();
    }

    private void initCommands()
    {

        //Help Command
        Subcommands.put(Arrays.asList("help"), CommandSpec.builder()
            .description(Text.of("显示所有可用的命令"))
            .permission(PluginPermissions.HELP_COMMAND)
            .executor(new HelpCommand())
            .build());

        //Create Command
        Subcommands.put(Arrays.asList("c", "create"), CommandSpec.builder()
            .description(Text.of("切换至箱子创建模式"))
            .permission(PluginPermissions.CREATE_COMMAND)
            .arguments(GenericArguments.optional(GenericArguments.string(Text.of("箱子名称"))))
            .executor(new CreateCommand())
            .build());

        //Remove Command
        Subcommands.put(Arrays.asList("r", "remove"), CommandSpec.builder()
            .description(Text.of("切换至箱子移除模式"))
            .permission(PluginPermissions.REMOVE_COMMAND)
            .executor(new RemoveCommand())
            .build());

        //Update Command
        Subcommands.put(Arrays.asList("u", "update"), CommandSpec.builder()
            .description(Text.of("切换至箱子更新模式"))
            .permission(PluginPermissions.UPDATE_COMMAND)
            .executor(new UpdateCommand())
            .build());

        //Time Command
        Subcommands.put(Arrays.asList("t", "time"), CommandSpec.builder()
            .description(Text.of("更改箱子重装时间"))
            .permission(PluginPermissions.TIME_COMMAND)
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("时间"))))
            .executor(new TimeCommand())
            .build());

        //List Command
        Subcommands.put(Arrays.asList("l","list"), CommandSpec.builder()
                .description(Text.of("显示所有可重装的箱子"))
                .permission(PluginPermissions.LIST_COMMAND)
                .executor(new ListCommand())
                .build());

        //Refill Command
        Subcommands.put(Arrays.asList("refill"), CommandSpec.builder()
                .description(Text.of("强制重装一个箱子"))
                .permission(PluginPermissions.REFILL_COMMAND)
                .arguments(new ContainerNameArgument(Text.of("箱子名称")))
                .executor(new RefillCommand())
                .build());

        //RefillAll Command
        Subcommands.put(Arrays.asList("refillall"), CommandSpec.builder()
                .description(Text.of("强制重装所有箱子"))
                .permission(PluginPermissions.REFILLALL_COMMAND)
                .executor(new RefillAllCommand())
                .build());

        Subcommands.put(Arrays.asList("setname"), CommandSpec.builder()
                .description(Text.of("为一个可重装的箱子设置名称"))
                .permission(PluginPermissions.SETNAME_COMMAND)
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("名称"))))
                .executor(new SetnameCommand())
                .build());

        //Build all commands
        CommandSpec mainCommand = CommandSpec.builder()
                .description(Text.of("显示所有可用的命令"))
                .executor(new HelpCommand())
                .children(Subcommands)
                .build();

        //Register commands
        Sponge.getCommandManager().register(this, mainCommand, "chestrefill", "cr");

    }

    private void initListeners()
    {
        Sponge.getEventManager().registerListeners(this, new RightClickListener());
        Sponge.getEventManager().registerListeners(this, new ContainerBreakListener());
        Sponge.getEventManager().registerListeners(this, new PlayerJoinListener());
//        Sponge.getEventManager().registerListeners(this, new DropItemListener());
    }
}
