package br.com.nareba.nprotect.region.event;

import br.com.nareba.nprotect.region.RegionManager;
import br.com.nareba.nprotect.region.core.RegionStatus;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.block.BlockTrapdoor;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.Optional;

public class RegionEvent implements Listener {
    private final RegionManager regionManager;
    public RegionEvent(RegionManager regionManager)   {
        this.regionManager = regionManager;
    }
    @EventHandler //Region Break
    public void onBlockBreak(BlockBreakEvent event)   {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Optional<RegionStatus> optBool = regionManager.can(player, block, "break");
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode quebrar aqui!");
                event.setCancelled();
            }
        }
    }
    @EventHandler //Region Place
    public void onBlockPlace(BlockPlaceEvent event)   {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Item item = event.getItem();
        Optional<RegionStatus> optBool = regionManager.can(player, block, "place");
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode colocar blocos aqui!");
                event.setCancelled();
            }
        }
        if (item.hasCustomBlockData())   {
            if (item.getCustomName().equals("Bloco de Proteção") && item.getCustomBlockData().contains("protection_size"))   {
                Optional<RegionStatus> optStatus = regionManager.createRegion(player, item, block);
                if (optStatus.isPresent())   {
                    if (optStatus.get() == RegionStatus.CANT)   {
                        player.sendMessage("Você não pode criar uma região aqui!");
                        event.setCancelled();
                    }
                }
            }
        }
    }
    @EventHandler //Region Interact
    public void onPlayerInteract(PlayerInteractEvent event)   {
        Player player = event.getPlayer();
        Vector3 beforePos = new Vector3(player.x, player.y, player.z);
        Block block = event.getBlock();
        if (block.canBeActivated() && block.getId() != Block.DIRT && block.getId() != Block.GRASS)   {
            Optional<RegionStatus> optBool = regionManager.can(player, block, "use");
            if (optBool.isPresent())   {
                if (optBool.get() == RegionStatus.CANT)   {
                    switch(block.getId())   {
                        case Block.DOOR_BLOCK:
                            BlockDoor door = (BlockDoor) block;
                            door.toggle(player);
                            door.toggle(player);
                            if (!door.isOpen())   {
                                player.teleport(beforePos);
                            }
                            break;
                        case Block.TRAPDOOR:
                            BlockTrapdoor trapDoor = (BlockTrapdoor) block;
                            if (!trapDoor.isOpen())   {
                                player.teleport(beforePos);
                            }
                            break;
                        case Block.FENCE_GATE:
                            BlockFenceGate fenceGate = (BlockFenceGate) block;
                            if (!fenceGate.isOpen())   {
                                player.teleport(beforePos);
                            }
                            break;
                    }
                    player.sendMessage("você não pode usar blocos aqui!");
                    event.setCancelled();
                }
            }
        }
    }
    @EventHandler //Region Move
    public void onPlayerMove(PlayerMoveEvent event)   {
        Player player = event.getPlayer();
        Position playerPos = player.getPosition();
        Optional<RegionStatus> optBool = regionManager.can(player, player.getPosition(), "move");
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode se mover aqui!");
                event.setCancelled();
            }
        }
    }
}
