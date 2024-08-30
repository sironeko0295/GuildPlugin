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
    public static List<String> GuildName = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if(strings.length < 1){
            commandSender.sendMessage(ChatColor.RED + "コマンド引数が不足しています");
        }

        if(strings[0].equalsIgnoreCase("create")){
            createCommand(commandSender, strings);

        } else if (strings[0].equalsIgnoreCase("join")) {
            joinCommand(commandSender, strings);
        }else if(strings[0].equalsIgnoreCase("leave")){
            leaveCommand(commandSender, strings);
        }
        return false;
    }

    public void createCommand(CommandSender commandSender, String[] strings){
        if(!commandSender.isOp()){
            commandSender.sendMessage(ChatColor.RED + "権限がありません");
        }

        String name = strings[1];

        if(strings.length == 2){
            if (GuildName.contains(name)){
                commandSender.sendMessage(ChatColor.RED + "作成しようとしたギルド名はすでに使用されています");
                return;
            }
        }

        String joinName = strings[2];

        if(strings.length == 3){
            Player targetPlayer = Bukkit.getPlayer(joinName);
            Set<String> target_tag = targetPlayer.getScoreboardTags();
            target_tag.retainAll(GuildName);

            targetPlayer.addScoreboardTag(name);

            if(!target_tag.isEmpty()){
                commandSender.sendMessage(ChatColor.RED + (targetPlayer.getName() + "はすでに他のギルドに所属しています"));
                return;
            }

            GuildName.add(name);
            targetPlayer.addScoreboardTag("master");
            targetPlayer.addScoreboardTag(name);
            commandSender.sendMessage(ChatColor.GREEN + name + "が作成されました");
        }
    }

    public void joinCommand(CommandSender commandSender, String[] strings){
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
            target_tag.retainAll(GuildName);

            if(!target_tag.isEmpty()){
                commandSender.sendMessage(ChatColor.RED + (targetPlayer.getName() + "はすでに他のギルドに所属しています"));
                return;
            }

            for(String tag: guild_tag){
                targetPlayer.addScoreboardTag(tag);
            }
        }
    }

    public void leaveCommand(CommandSender commandSender, String[] strings){
        if(!(commandSender.hasPermission("master") || commandSender.hasPermission("R4"))){
            commandSender.sendMessage(ChatColor.RED + "権限がありません");

            return;
        }

        if(strings.length < 2){
            commandSender.sendMessage(ChatColor.RED + "コマンド引数が不足しています");
        }

        String pPlayer = strings[0];

        if(strings.length == 2){
            Player targetPlayer = Bukkit.getPlayer(pPlayer);
            Set<String> tag = targetPlayer.getScoreboardTags();
            tag.retainAll(GuildName);

            if(!tag.isEmpty()){
                for(String string: tag){
                    targetPlayer.removeScoreboardTag(string);
                }

                if()
            }
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggest = new ArrayList<>();
        if(strings.length == 1){
            suggest.add("join");
            suggest.add("create");
        } else if ((strings.length == 2 && strings[0].equalsIgnoreCase("join")) || (strings.length == 3 && strings[0].equalsIgnoreCase("create"))) {
            for(Player player: Bukkit.getOnlinePlayers()){
                suggest.add(player.getName());
            }
        }

        return suggest;
    }
}
