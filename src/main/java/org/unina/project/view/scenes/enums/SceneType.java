package org.unina.project.view.scenes.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.unina.project.view.scenes.BasicScene;
import org.unina.project.view.scenes.Scene;

/**
 * Definizione delle interfacce comuni e definite
 * tramite file FXML dell'applicazione.
 */
@RequiredArgsConstructor
public enum SceneType {
    LOGIN("Accedi alla piattaforma", "login"),
    HOMEPAGE("Esplora i gruppi", "homepage"),
    GROUP("Condividi i tuoi pensieri e le tue esperienze", "group"),
    NOTIFICATIONS("Notifiche ricevute", "notifications"),
    REPORT("Visualizzazione report", "report");

    @Getter
    private final String name, fxml;

    public Scene getScene() {
        return new BasicScene(this);
    }
}
