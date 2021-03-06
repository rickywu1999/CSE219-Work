package ttt.gui_better;

import javafx.scene.layout.ColumnConstraints;
import ttt.engine.TTTEngine;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Graphical Tic-Tac-Toe board.
 *
 * @author Eugene Stark
 * @version 20180211
 */
public class TTTBoard extends GridPane {

    private final TTTAppGUI gui;
    private final TTTEngine engine;
    private final int dim;
    private static final int BOARD_DIM = 3;
    private static final int BUTTON_SIZE = 50;

    public TTTBoard(TTTAppGUI app, TTTEngine engine) {
        this.gui = app;
        this.engine = engine;
        this.dim = engine.getDim();
        setConstraints();
        addButtons();
    }

    private void setConstraints() {
        for (int i = 0; i < BOARD_DIM; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            this.getRowConstraints().add(rc);
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            this.getColumnConstraints().add(cc);
        }

    }

    private void addButtons() {
        for (int i = 0; i < BOARD_DIM; i++) {
            for (int j = 0; j < BOARD_DIM; j++) {
                TTTButton btn = new TTTButton(gui,engine,i,j);
                this.add(btn,i,j);
            }
        }
    }

}
