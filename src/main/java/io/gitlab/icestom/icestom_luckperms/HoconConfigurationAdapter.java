package io.gitlab.icestom.icestom_luckperms;

import me.lucko.luckperms.common.config.generic.adapter.ConfigurateConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.ConfigurationAdapter;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HoconConfigurationAdapter extends ConfigurateConfigAdapter implements ConfigurationAdapter {

    public HoconConfigurationAdapter(LuckPermsPlugin plugin) {
        super(plugin, resolveConfig(plugin, "luckperms.conf"));
    }

    private static Path resolveConfig(LuckPermsPlugin plugin, String fileName) {
        Path file = plugin.getBootstrap().getConfigDirectory().resolve(fileName);
        if (Files.exists(file)) {
            return file;
        }

        try {
            Files.createDirectories(file.getParent());
            try (InputStream in = HoconConfigurationAdapter.class.getResourceAsStream("/" + fileName)) {
                if (in != null) {
                    Files.copy(in, file);
                } else {
                    Files.createFile(file);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    @Override
    protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
        return HoconConfigurationLoader.builder().setPath(path).build();
    }
}