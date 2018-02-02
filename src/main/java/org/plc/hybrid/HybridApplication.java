package org.plc.hybrid;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class HybridApplication extends AbstractHybridApplication {
    @Override
    protected Pane initUI() throws IOException {
        Pane root;
        // UI part of the decoration

        FXMLLoader fxmlLoader = new FXMLLoader(HybridApplication.class.getResource("HybridApplication.fxml"));
        fxmlLoader.setController(this);
        root = fxmlLoader.load();

        return root;
    }

    @Override
    protected String getHTMLPage() {
        return HybridApplication.class.getResource("hybridPage.html").toExternalForm();
    }
}
