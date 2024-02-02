package org.unina.project.view.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.*;

/**
 * FXML Controller per le interfacce contenenti delle entità sotto forma di "card".
 * Ad esempio la pagina delle notifiche, dei post di un gruppo e la homepage contenente
 * i gruppi disponibili sulla piattaforma.
 * @param <T> tipo di entità
 */
@Getter
public abstract class FXMLEntityGridController<T> extends FXMLGeneralController {
    private final Map<T, VBox> cards = new HashMap<>();
    @Setter
    private boolean isReversedOrder = false;
    @FXML
    private GridPane grid;

    /**
     * Aggiorna l'interfaccia con tutte le "card"
     * associate alle entità di questa pagina.
     */
    protected void refresh() {
        this.cards.clear();
        for (T entity : getAllEntities()) {
            VBox card = getEntityCard(entity);
            this.cards.put(entity, card);
        }
    }

    /**
     * Mostra tutte le "card" delle entità.
     */
    protected void show() {
        show(this.cards.values().stream().toList());
    }

    /**
     * Recupera tutte le entità di interesse per questa pagina.
     * @return set contenente le entità recuperate
     */
    protected abstract Set<T> getAllEntities();

    /**
     * Ottieni l'elemento "card" rappresentativo per una singola entità.
     * @param entity associata all'elemento
     * @return elemento grafico rappresentativo
     */
    protected abstract VBox getEntityCard(T entity);

    /**
     * Pulisci la griglia contenente tutti gli elementi rappresentativi
     * delle entità gestite da questo controller, e mostra i nuovi elementi
     * specificati nei parametri di questo metodo.
     * @param cards da mostrare
     */
    protected void show(List<VBox> cards) {
        this.grid.getChildren().removeAll(this.cards.values().toArray(Node[]::new));
        LinkedList<VBox> ordered = new LinkedList<>(cards);
        ordered.sort(Comparator.comparingLong(node -> (long) node.getProperties().getOrDefault("date", new Timestamp(0))));
        if (isReversedOrder)
            ordered = ordered.reversed();
        for (int row = 0; row < ordered.size(); row++)
            this.grid.addRow(row, ordered.get(row));
    }
}
