package org.unina.project.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;

/**
 * Questa classe si occupa di serializzare e deserializzare gli oggetti in stringhe JSON e viceversa.
 */
public interface Mapper {
    /**
     * Istanza principale del mappatore di oggetti Jackson
     */
    ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    /**
     * Deserializza un oggetto partendo da un file JSON.
     * @param file da deserializzare
     * @param clazz classe dell'oggetto risultante
     * @return l'oggetto deserializzato
     * @param <T> tipo dell'oggetto di ritorno
     * @throws RuntimeException nel caso in cui sia impossibile deserializzare il file JSON.
     */
    @SneakyThrows
    static <T> T fromJson(File file, Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Impossibile leggere il file %s e trasformarlo in %s", file.getName(), clazz.getSimpleName()), e);
        }
    }
}
