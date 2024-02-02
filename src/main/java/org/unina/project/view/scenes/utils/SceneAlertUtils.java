package org.unina.project.view.scenes.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.Nullable;
import org.unina.project.App;

public final class SceneAlertUtils {
    /**
     * Crea un nuovo {@link Alert}.
     * @param type di avviso
     * @param title della schermata dell'avviso
     * @param header dell'avviso
     * @param content dell'avviso
     * @param wait true se bisogna attendere un risultato
     * @param buttonTypes cliccabili nell'avviso
     * @return nuovo {@link Alert}
     */
    public static Alert alert(Alert.AlertType type, String title, String header, String content, boolean wait, @Nullable ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        alert.initOwner(App.getInstance().getStage());
        alert.setTitle(String.format("%s - %s", App.getInstance().getName(), title));
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (buttonTypes != null)
            alert.getButtonTypes().addAll(buttonTypes);
        if (wait) alert.showAndWait();
        else alert.show();
        return alert;
    }
}
