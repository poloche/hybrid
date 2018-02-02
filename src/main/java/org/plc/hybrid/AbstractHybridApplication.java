package org.plc.hybrid;

import insidefx.ibreed.JavaScriptBridge;
import insidefx.ibreed.WebViewInjector;
import insidefx.undecorator.Undecorator;
import insidefx.undecorator.UndecoratorScene;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class AbstractHybridApplication extends Application {

    private static final Logger LOOGER = LogManager.getLogger(AbstractHybridApplication.class);
    private UndecoratorScene undecoratorScene;
    private JavaScriptBridge javaScriptBridge;
    @FXML
    private WebView webView;
    private String applicationTitle = "";

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationTitle(String applicationTitle) {
        this.applicationTitle = applicationTitle;
    }


    @Override
    public void start(Stage stage) throws Exception {
        Pane root = initUI();
        undecoratorScene = new UndecoratorScene(stage, root);
        undecoratorScene.setFadeInTransition();
        setAsHybrid(stage);
        stage.setTitle(applicationTitle);
        stage.setScene(undecoratorScene);
        stage.sizeToScene();
        stage.toFront();
        stage.centerOnScreen();

        /*
         * Fade transition on window closing request
         */
        stage.setOnCloseRequest(eventHandler -> {
            eventHandler.consume();    // Do not hide
            undecoratorScene.setFadeOutTransition();
        });

        // Set minimum size based on client area's minimum sizes
        Undecorator undecorator = undecoratorScene.getUndecorator();
        stage.setMinWidth(undecorator.getMinWidth());
        stage.setMinHeight(undecorator.getMinHeight());

        stage.setWidth(undecorator.getPrefWidth());
        stage.setHeight(undecorator.getPrefHeight());
        if (undecorator.getMaxWidth() > 0) {
            stage.setMaxWidth(undecorator.getMaxWidth());
        }
        if (undecorator.getMaxHeight() > 0) {
            stage.setMaxHeight(undecorator.getMaxHeight());
        }
        stage.show();
    }

    protected abstract Pane initUI() throws IOException;

    private void setAsHybrid(Stage stage) {
        // The generic object for JS and JavaFX interop
        javaScriptBridge = new JavaScriptBridge(webView.getEngine());
        javaScriptBridge.fromJSProperty.addListener(new ChangeListener<String>() {
            FadeTransition fillTransition;

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                // Something happened on JS side, so show it!
                if (fillTransition != null) {
                    fillTransition.playFromStart();
                } else {
                    fillTransition = new FadeTransition();
//                    fillTransition.setNode(keyUp);
                    fillTransition.setDuration(Duration.millis(150));
                    fillTransition.setCycleCount(2);
                    fillTransition.setFromValue(0.2);
                    fillTransition.setToValue(1);
                    fillTransition.setAutoReverse(true);
                    fillTransition.play();
                }
            }
        });

        // WebView customization (handlers...)
        WebViewInjector.inject(stage, webView, javaScriptBridge);
        // Move the stage when a drag is detected in the webview
        //undecoratorScene.setAsStageDraggable(stage, webView);

        // Hide URL textfield on main UI if needed
//        urlTxt.setVisible(!Boolean.getBoolean("ibreed.hideURL"));

        // Default URL to load
        final String url = getHTMLPage();
        LOOGER.info("Loading: ", url);

        webView.getEngine().load(url);

        // Reflect the current URL in the text field
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (observableValue, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
//TODO: should implement loading page
                    } else if (newState == Worker.State.FAILED) {
//TODO: should implement error load page
                        LOOGER.error("Error while loading: ", url);
                    }
                });

    }

    protected abstract String getHTMLPage();
}
