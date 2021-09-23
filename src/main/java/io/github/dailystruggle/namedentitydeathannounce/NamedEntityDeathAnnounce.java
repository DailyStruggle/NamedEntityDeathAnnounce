package io.github.dailystruggle.namedentitydeathannounce;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class NamedEntityDeathAnnounce extends JavaPlugin {
    private static NamedEntityDeathAnnounce instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(new DetectNamedEntityDeath(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static class DetectNamedEntityDeath implements Listener {
        @EventHandler(priority = EventPriority.LOW)
        public void onEntityDeath(EntityDeathEvent event){
            if(event.getEntity() instanceof Player) return;
            if(event.getEntityType().name().equals(event.getEntity().getName().toUpperCase())) return;
            Location location = event.getEntity().getLocation();
            Bukkit.getScheduler().runTaskAsynchronously(instance,() -> {
                String msg = ChatColor.of("#A020F0") + "[server] named entity " + event.getEntity().getName() + " has died";
                String hoverMsg = ChatColor.of("#D4AF37") + "";
                Player killer = event.getEntity().getKiller();
                if(killer!=null) {
                    hoverMsg += "killer:" + killer.getName() + "  ";
                }
                EntityDamageEvent cause = event.getEntity().getLastDamageCause();
                if(cause!=null) {
                    hoverMsg += "cause:" + event.getEntity().getLastDamageCause().getCause().name() + "  ";
                }
                hoverMsg += "x:" + location.getBlockX() + "  y:" + location.getBlockY() + "  z:" + location.getBlockZ();
                BaseComponent[] playerMsg = TextComponent.fromLegacyText(msg);
                BaseComponent[] hover = TextComponent.fromLegacyText(hoverMsg);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover));
                for(BaseComponent component : playerMsg){
                    component.setHoverEvent(hoverEvent);
                }
                Bukkit.getLogger().log(Level.INFO,msg);
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(!player.hasPermission("NamedEntityDeathAnnounce.view")) continue;
                    player.spigot().sendMessage(playerMsg);
                }
            });
        }
    }
}
