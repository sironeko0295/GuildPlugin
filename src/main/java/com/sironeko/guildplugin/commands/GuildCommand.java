package com.sironeko.guildplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuildCommand implements CommandExecutor, TabCompleter {
    public static List<String> suggest = new ArrayList<>();
    public static List<String> GuildName = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if(strings.length < 1){
            commandSender.sendMessage(ChatColor.RED + "コマンド引数が不足しています");
        }

        if(strings[0].equalsIgnoreCase("create")){
            createCommand(commandSender, command, s, strings);

        } else if (strings[0].equalsIgnoreCase("join")) {
            joinCommand(commandSender, command, s, strings);
        }

        return false;
    }

    public void createCommand(CommandSender commandSender, Command command, String s, String[] strings){
        if(!commandSender.isOp()){
            commandSender.sendMessage(ChatColor.RED + "権限がありません");
        }

        if(strings.length == 2){
            String name = strings[1];

            if (GuildName.contains(name)){
                commandSender.sendMessage(ChatColor.RED + "作成しようとしたギルド名はすでに使用されています");

                return;
            }

            GuildName.add(name);
            commandSender.sendMessage(ChatColor.GREEN + name + "が作成されました");

        }
    }

    public void joinCommand(CommandSender commandSender, Command command, String s, String[] strings){
        if(!(commandSender.hasPermission("master") || commandSender.hasPermission("R4"))){
            commandSender.sendMessage(ChatColor.RED + "権限がありません");
        }

        String playerName = strings[1];

        if(strings.length == 2){
            Player playerTag = (Player)commandSender;
            Player targetPlayer = Bukkit.getPlayer(playerName);
            Set<String> guild_tag = playerTag.getScoreboardTags();
            Set<String> target_tag = targetPlayer.getScoreboardTags();

            guild_tag.retainAll(GuildName);

            if(guild_tag.isEmpty()){
                commandSender.sendMessage(ChatColor.RED + "あなたはギルドに所属していません");

                return;
            }else if(!target_tag.isEmpty()){
                commandSender.sendMessage(ChatColor.RED + (targetPlayer.getName() + "はすでに他のギルドに所属しています"));
                return;
            }

            for(String tag: guild_tag){
                targetPlayer.addScoreboardTag(tag);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            suggest.add("join");
            suggest.add("create");
        } else if (strings.length == 2) {
            for(Player player: Bukkit.getOnlinePlayers()){
                suggest.add(player.getName());
            }
        }

        return suggest;
    }
}
