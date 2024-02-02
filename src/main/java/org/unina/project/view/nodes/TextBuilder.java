package org.unina.project.view.nodes;

import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Builder per la creazione veloce di nuovi {@link Text}.
 */
public class TextBuilder {
    private String content, color = "#717171", font = "Verdana";
    private double size = 16D, strokeWidth = 0D;
    private FontWeight weight = FontWeight.NORMAL;
    private FontSmoothingType smoothingType = FontSmoothingType.LCD;
    private StrokeType strokeType = StrokeType.OUTSIDE;

    /**
     * Metodo statico per creare una nuova istanza di questo builder
     * con il contenuto del testo già impostato.
     * @param content da impostare
     * @return nuova istanza di Text builder
     */
    public static TextBuilder of(String content) {
        return new TextBuilder().content(content);
    }

    /**
     * Imposta il contenuto di questo {@link Text}
     * @param content da impostare
     * @return questo TextBuilder
     */
    public TextBuilder content(String content) {
        this.content = content;
        return this;
    }

    /**
     * Imposta il colore di questo {@link Text}
     * @param color da impostare
     * @return questo TextBuilder
     */
    public TextBuilder color(String color) {
        this.color = color;
        return this;
    }

    /**
     * Imposta il font di questo {@link Text}
     * @param font da impostare
     * @return questo TextBuilder
     */
    public TextBuilder font(String font) {
        this.font = font;
        return this;
    }

    /**
     * Imposta la grandezza di questo {@link Text}
     * @param size da impostare
     * @return questo TextBuilder
     */
    public TextBuilder size(double size) {
        this.size = size;
        return this;
    }

    /**
     * Imposta lo spessore di questo {@link Text}
     * @param weight da impostare
     * @return questo TextBuilder
     */
    public TextBuilder weight(FontWeight weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Imposta il tipo di smussamento di questo {@link Text}
     * @param smoothingType da impostare
     * @return questo TextBuilder
     */
    public TextBuilder smoothing(FontSmoothingType smoothingType) {
        this.smoothingType = smoothingType;
        return this;
    }

    /**
     * Imposta il contorno di questo {@link Text}
     * @param strokeType da impostare
     * @return questo TextBuilder
     */
    public TextBuilder strokeType(StrokeType strokeType) {
        this.strokeType = strokeType;
        return this;
    }

    /**
     * Imposta la grandezza del contorno di questo {@link Text}
     * @param strokeWidth da impostare
     * @return questo TextBuilder
     */
    public TextBuilder strokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    /**
     * Crea una nuova istanza du un {@link Text} partendo da questo builder.
     * @return nuova istanza di Text
     */
    public @Nullable Text build() {
        if (content == null)
            throw new IllegalStateException("Non è possibile creare un testo senza contenuto.");
        Text text = new Text(content);
        text.setFontSmoothingType(smoothingType);
        text.setFill(Paint.valueOf(color));
        text.setStrokeType(strokeType);
        text.setStrokeWidth(strokeWidth);
        text.setFont(Font.font(font, weight, size));
        return text;
    }
}
