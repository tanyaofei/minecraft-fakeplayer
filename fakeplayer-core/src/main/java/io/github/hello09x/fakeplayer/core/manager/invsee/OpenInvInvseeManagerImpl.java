package io.github.hello09x.fakeplayer.core.manager.invsee;

import com.google.common.base.Throwables;
import com.lishid.openinv.IOpenInv;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * @author tanyaofei
 * @since 2024/8/12
 **/
public class OpenInvInvseeManagerImpl extends AbstractInvseeManager {

    private final static Logger log = Main.getInstance().getLogger();
    private final IOpenInv openInv;

    public OpenInvInvseeManagerImpl(FakeplayerManager manager, FakeplayerList fakeplayerList) {
        super(manager, fakeplayerList);
        this.openInv = (IOpenInv) Bukkit.getPluginManager().getPlugin("OpenInv");
    }

    @Override
    protected InventoryView openInventory(@NotNull Player viewer, @NotNull Player whom) {
        try {
            return openInv.openInventory(viewer, openInv.getSpecialInventory(whom, true));
        } catch (InstantiationException e) {
            log.warning("Failed to %s's open inventory for %s\n%s".formatted(whom.getName(), viewer.getName(), Throwables.getStackTraceAsString(e)));
            return null;
        }
    }
}
