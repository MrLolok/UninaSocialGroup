package org.unina.project.view.nodes.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import org.unina.project.App;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.social.entities.users.User;
import org.unina.project.social.enums.GroupType;
import org.unina.project.social.sessions.Session;
import org.unina.project.view.scenes.enums.SceneType;
import org.unina.project.view.scenes.utils.SceneAlertUtils;

/**
 * Gestore dei click al bottone per accedere o visualizzare un gruppo.
 */
@RequiredArgsConstructor
public class GroupAccessButtonHandler implements EventHandler<ActionEvent> {
    private final Group group;
    private final User user;

    /**
     * Verifica che l'utente sia membro del gruppo e in tal caso mostra
     * la schermata del gruppo con tutti i post.
     * Altrimenti, se il gruppo è aperto viene creata una nuova associazione
     * tra il gruppo e il nuovo membro, e se il gruppo è a invito, crea una
     * nuova richiesta di accesso.
     * @param event relativo al click del bottone
     */
    @Override
    public void handle(ActionEvent event) {
        GroupType type = group.getType();
        if (!(event.getSource() instanceof Button button)) return;
        if (group.isMember(user)) {
            Session.getGlobalSession().setGroup(group);
            App.getInstance().setScene(SceneType.GROUP.getScene());
            return;
        }
        if (type == GroupType.CLOSED)
            return;
        else if (type == GroupType.INVITE) {
            if (group.isRequester(user))
                SceneAlertUtils.alert(Alert.AlertType.WARNING, "Azione bloccata", "Richiesta inviata", "Hai già inviato una richiesta di accesso!", false);
            else
                group.getRequesters().add(user);
        } else { // type == GroupType.OPEN
            button.setText("Visualizza");
            group.getMembers().add(user);
        }
        App.getInstance().getGroups().update(group);
        SceneAlertUtils.alert(
                Alert.AlertType.INFORMATION, "Accesso al gruppo",
                type == GroupType.INVITE ? "Richiesta inviata" : "Accesso completato",
                type == GroupType.INVITE ? "Hai inviato una nuova richiesta di accesso!" : "Iscrizione al gruppo completata con successo!",
                false);
    }
}
