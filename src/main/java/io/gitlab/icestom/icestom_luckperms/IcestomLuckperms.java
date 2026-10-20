package io.gitlab.icestom.icestom_luckperms;

import io.gitlab.icestom.icestom.IceStom;
import io.gitlab.icestom.icestom.plugins.IceStomPlugin;
import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.UUID;

public class IcestomLuckperms implements IceStomPlugin {

    private static final Logger log = LoggerFactory.getLogger(IcestomLuckperms.class);

    private static IcestomLuckperms instance;

    private LuckPerms luckPerms;

    IcestomLuckperms() {
        instance = this;
    }

    @Override
    public void onEnable(EventNode<Event> eventNode) {

        Path directory = Path.of("luckperms");

        luckPerms = LuckPermsMinestom.builder(directory)
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(plugin -> new MultiConfigurationAdapter(plugin,
                        new EnvironmentVariableConfigAdapter(plugin),
                        new HoconConfigurationAdapter(plugin)
                )).permissionSuggestions("icestom.perfhud", "icestom.timetrial.bypass_filter", "icestom.panel")
                .enable();

        IceStom.getInstance().setPermissionHandler(this::evaluate);

        log.info("IcestomLuckperms Enabled!");
    }

    private boolean evaluate(UUID uuid, String permission) {
        User user = luckPerms.getUserManager()
                .getUser(uuid);

        if (user == null) {
            log.warn("Failed to pull use checking permissions for {}", uuid);
            return false;
        }

        return user.getCachedData()
                .getPermissionData()
                .checkPermission(permission) == Tristate.TRUE;
    }

    @Override
    public void onStart() {}

    public static IcestomLuckperms getInstance() {
        return instance;
    }
}
