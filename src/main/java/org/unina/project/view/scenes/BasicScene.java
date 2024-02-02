package org.unina.project.view.scenes;

import javafx.scene.Parent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.view.scenes.enums.SceneType;

/**
 * Implementazione basica di una schermata dell'applicazione.
 */
@RequiredArgsConstructor
public class BasicScene implements Scene {
    // Tipo di schermata
    private final SceneType sceneType;

    @Override
    public void show(Stage stage) {
        setRoot(stage);
        stage.setTitle(String.format("%s - %s", App.getInstance().getName(), sceneType.getName()));
        stage.show();
    }

    /**
     * Ottieni o crea l'interfaccia definita nel file FXML per poi
     * impostarla allo {@link Stage} principale dell'applicazione.
     * @param stage da impostare
     */
    private void setRoot(Stage stage) {
        Parent root = getFXMLFile(sceneType.getFxml());
        if (stage.getScene() == null) {
            javafx.scene.Scene scene = new javafx.scene.Scene(root, App.getInstance().getWidth(), App.getInstance().getHeight());
            stage.setScene(scene);
        } else stage.getScene().setRoot(root);
    }
}
