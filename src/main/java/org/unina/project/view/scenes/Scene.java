package org.unina.project.view.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Questa classe rappresenta una schermata dell'applicazione.
 * @see javafx.scene.Scene
 */
@FunctionalInterface
public interface Scene {
    /**
     * Carica e mostra la schermata nel box dell'applicazione.
     * @param stage dell'applicazione
     */
    void show(Stage stage);

    /**
     * Recupera il file FXML relativo a questa schermata.
     * @param fxml della schermata
     * @return file trovato
     */
    default Parent getFXMLFile(String fxml) {
        try {
            return new FXMLLoader().load(getClass().getClassLoader().getResourceAsStream(String.format("%s.fxml", fxml)));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load scene", e);
        }
    }
}
