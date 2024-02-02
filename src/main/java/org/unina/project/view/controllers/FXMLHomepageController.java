package org.unina.project.view.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.unina.project.App;
import org.unina.project.social.entities.groups.Group;
import org.unina.project.view.nodes.custom.GroupCard;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FXML Controller per l'interfaccia di homepage.
 * Mostra le "card" contenenti le informazioni su tutti i gruppi.
 */
public class FXMLHomepageController extends FXMLEntityGridController<Group> {
    @FXML
    private TextField search;

    @Override
    protected void setup() {
        refresh();
        show();
    }

    @Override
    protected Set<Group> getAllEntities() {
        return App.getInstance().getGroups().getAll();
    }

    @Override
    protected VBox getEntityCard(Group group) {
        return new GroupCard(group).create();
    }

    @FXML
    public void filter() {
        String filter = this.search.getText();
        List<VBox> cards = getFilteredGroups(filter);
        show(cards);
    }

    private List<VBox> getFilteredGroups(String filter) {
        return getCards().entrySet().stream().filter(entry -> {
                    Group group = entry.getKey();
                    return group.getName().toLowerCase().contains(filter) || group.getTags().stream().anyMatch(tag -> tag.getKeyword().toLowerCase().contains(filter));
                })
                .map(Map.Entry::getValue)
                .toList();
    }
}
