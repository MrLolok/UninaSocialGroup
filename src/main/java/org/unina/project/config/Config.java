package org.unina.project.config;

import org.unina.project.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * Questa classe rappresenta una sorta di Wrapper per una configurazione del software.
 * Inoltre, implementa metodi per caricare e ottenere un file di configurazione.
 * @param config class contenente tutte le informazioni inerenti al proprio contesto
 * @param <T> tipo di config
 */
public record Config<T> (T config) {
    private final static Logger LOGGER = Logger.getLogger("UninaSocialGroup-Configs");

    /**
     * Cerca ed eventualmente carica il contenuto default
     * del file di configurazione specificato per nome.
     * @param name del file di configurazione
     * @return file di configurazione
     */
    public static File getFile(String name) {
        File file = new File("configs", String.format("%s.json", name));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream in = Config.class.getClassLoader().getResourceAsStream(String.format("configs/%s.json", name))) {
                if (in == null)
                    LOGGER.severe(String.format("Non è stato trovato il file %s all'interno del progetto.", name));
                else
                    Files.copy(in, file.toPath());
            } catch (IOException e) {
                LOGGER.warning("Impossibile copiare la configurazione predefinita. - File: " + name);
            }
        }
        return file;
    }

    /**
     * Carica una configurazione del software
     * @param name del file di configurazione
     * @param clazz della configurazione
     * @return istanza contente le configurazioni cercate
     * @param <T> tipo di config
     */
    public static <T> Config<T> load(String name, Class<T> clazz) {
        return new Config<>(Mapper.fromJson(getFile(name), clazz));
    }
}
