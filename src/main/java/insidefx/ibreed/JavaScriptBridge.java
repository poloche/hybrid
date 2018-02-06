package insidefx.ibreed;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic 2 ways communication bridge (JavaScript 2 Java and vice versa)
 */
public class JavaScriptBridge {
    private static final Logger LOOGER = LoggerFactory.getLogger(JavaScriptBridge.class);
    String fromJS;
    public SimpleStringProperty fromJSProperty;
    WebEngine webEngine;

    public JavaScriptBridge(WebEngine we) {
        webEngine = we;
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) ->
                LOOGER.error(" [{}]: {} :{}  ", sourceId, lineNumber, message)
        );

        fromJSProperty = new SimpleStringProperty(fromJS);
        enableConsole();
    }

    /**
     * Call from html page. Set the value into the property in order to fire
     * change events
     *
     * @param msgFromJS the message that will be delivered to JavaScript
     */
    public void sendFromJS(String msgFromJS) {
        fromJSProperty.set(msgFromJS);
    }

    /**
     * Execute the "jsToExecute" on the current page
     *
     * @param jsToExecute instruction that javascript want to java execute
     * @return the returned value from JavaScript if any
     */
    public Object sendToJS(String jsToExecute) {

        return webEngine.executeScript(jsToExecute);
    }

    private void enableConsole() {
        JSObject window = (JSObject) webEngine.executeScript("window");

        window.setMember("java", this);
        webEngine.executeScript("console.log = function(message){java.log(message);}");
    }

    public void log(String message) {
        LOOGER.info(message);
    }
}
