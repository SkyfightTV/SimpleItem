package fr.skyfighttv.simpleitem;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class SimpleItem extends ItemCreator {
    private static final HashMap<SimpleItem, TriConsumer<Player, SimpleItem, Event>> consumer = new HashMap<>();

    public SimpleItem(Material m) {
        super(m);
    }

    public SimpleItem(ItemStack is) {
        super(is);
    }

    public SimpleItem(Material m, int amount) {
        super(m, amount);
    }

    public SimpleItem(Material m, int amount, byte durability) {
        super(m, amount, durability);
    }

    public static void init(JavaPlugin instance) {
        instance.getServer().getPluginManager().registerEvents(new Listeners(), instance);
    }

    public SimpleItem onClick(TriConsumer<Player, SimpleItem, Event> consumers) {
        consumer.put(this, consumers);
        return this;
    }

    private static class Listeners implements Listener {
        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getCurrentItem() == null)
                return;
            consumer.keySet().forEach(simpleItem -> {
                if (event.getCurrentItem().isSimilar(simpleItem.toItemStack()))
                    consumer.get(simpleItem).accept((Player) event.getWhoClicked(), simpleItem, event);
            });
        }

        @EventHandler
        private void onInteract(PlayerInteractEvent event) {
            if (event.getItem() == null)
                return;
            consumer.keySet().forEach(simpleItem -> {
                if (event.getItem().isSimilar(simpleItem.toItemStack()))
                    consumer.get(simpleItem).accept(event.getPlayer(), simpleItem, event);
            });
        }

        @EventHandler
        private void onInteractEntity(PlayerInteractEntityEvent event) {
            consumer.keySet().forEach(simpleItem -> {
                if (event.getPlayer().getInventory().getItemInMainHand().isSimilar(simpleItem.toItemStack()))
                    consumer.get(simpleItem).accept(event.getPlayer(), simpleItem, event);
            });
        }
    }
}
