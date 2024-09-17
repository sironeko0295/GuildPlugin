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
        }else if(strings[0].equalsIgnoreCase("delete")){
            deleteCommand(commandSender, strings);
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

    public void leaveCommand(CommandSender commandSender, String[] strings) {
        // プレイヤーの権限チェック
        if (!(commandSender.hasPermission("master") || commandSender.hasPermission("R4"))) {
            commandSender.sendMessage(ChatColor.RED + "権限がありません");
            return;
        }

        // 引数の長さチェック
        if (strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "コマンド引数が不足しています");
            return;
        }

        String pPlayer = strings[0]; // 追放対象のプレイヤー名
        String player = commandSender.getName(); // コマンドを実行したプレイヤー名

        // プレイヤーの取得
        Player targetPlayer = Bukkit.getPlayer(pPlayer);
        Player commander = Bukkit.getPlayer(player);

        if (targetPlayer == null || commander == null) {
            commandSender.sendMessage(ChatColor.RED + "指定されたプレイヤーが存在しません");
            return;
        }

        // ギルドタグの取得
        Set<String> target_tag = targetPlayer.getScoreboardTags(); // 追放対象のギルドタグ
        Set<String> commander_tag = commander.getScoreboardTags(); // コマンド実行者のギルドタグ

        // ギルド名との共通タグを取得
        target_tag.retainAll(GuildName);
        commander_tag.retainAll(GuildName);

        // 追放対象プレイヤーがギルドに所属しているかチェック
        if (!target_tag.isEmpty()) {
            // コマンド実行者が同じギルドに所属しているかを確認
            if (!commander_tag.equals(target_tag)) {
                commandSender.sendMessage(ChatColor.RED + "異なるギルドに所属しているプレイヤーを追放できません");
                return;
            }

            // タグを削除
            for (String tag : target_tag) {
                targetPlayer.removeScoreboardTag(tag);
            }
            commandSender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + "はギルドを離脱しました");
        } else {
            commandSender.sendMessage(ChatColor.RED + "指定されたプレイヤーはギルドに所属していません");
        }
    }

    public void deleteCommand(CommandSender commandSender, String[] strings){
        if(!commandSender.hasPermission("master")){
            commandSender.sendMessage(ChatColor.RED + "権限がありません");

            return;
        }

        if(strings.length < 1){
            commandSender.sendMessage(ChatColor.RED + "コマンド引数が不足しています");
        }

        String commandSenderName = commandSender.getName();

        Player player = Bukkit.getPlayer(commandSenderName);
        Set<String> commander_tag = player.getScoreboardTags();
        commander_tag.retainAll(GuildName);

        if(!commander_tag.isEmpty()){
            GuildName.remove(commander_tag);

            commandSender.sendMessage(ChatColor.GREEN + "ギルドを削除しました");
        }

        commandSender.sendMessage(ChatColor.RED + "ギルドに所属していません");
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
