package top.catnies.deathtotem;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import dev.lone.itemsadder.api.ItemsAdder;

public class DeathListener implements Listener {

    // 当玩家死亡时触发
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws Exception {

        // 获取玩家对象
        Player player = event.getPlayer();
        World world = player.getWorld();

        // 判断玩家是否有权限,如果没则不执行 + 当前世界是否开启了死亡不掉落,如果开启了则不执行
        if (!player.hasPermission("deathtotem.use") || world.getGameRuleValue(GameRule.KEEP_INVENTORY)){
            return;
        }

        // 循环检查玩家背包内的物品,查看是否有死亡图腾
        for (ItemStack itemStack : player.getInventory().getContents()) {
            /*如果物品为空则跳过*/
            if (itemStack == null){
                continue;
            }
            /*获取配置文件里ItemsadderID*/
            String itemsAdderID = ConfigManager.tempConfig.getString("ItemsAdderID");
            if (itemsAdderID == null){
                throw new Exception("您的配置文件有误,没有检查到ItemsAdderID！请检查配置文件！");
            }
            /*检查物品是否是死亡图腾*/
            if (ItemsAdder.matchCustomItemName(itemStack,itemsAdderID)){
                /*如果有则扣除物品*/
                itemStack.setAmount(itemStack.getAmount() - 1);

                /*设置死亡不掉落*/
                event.setKeepInventory(true);
                event.getDrops().clear();

                /*设置死亡不掉落经验*/
                if (ConfigManager.tempConfig.getBoolean("KeepExp")){
                    event.setKeepLevel(true);
                    event.setDroppedExp(0);
                }

                /*播放动画*/
                if (ConfigManager.tempConfig.getBoolean("PlayAnimation")){
                    /*获取配置文件里AnimationID*/
                    String animationID = ConfigManager.tempConfig.getString("AnimationID");
                    if (animationID == null){
                        throw new Exception("您的配置文件有误,没有检查到AnimationID！请检查配置文件！");
                    }
                    /*播放动画*/
                    ItemsAdder.playTotemAnimation(player, animationID);
                }

                break;
            }
        }
    }
}