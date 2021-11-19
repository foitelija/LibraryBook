module com.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.rmi;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.logging.log4j;


    opens com.app to javafx.fxml;
    opens com.app.controller to javafx.fxml;
    opens com.app.model to javafx.fxml;
    opens com.app.service to javafx.fxml;
    exports com.app;
    exports  com.app.model;
}