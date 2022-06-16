module com.inventory.ims {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens others to javafx.base;
    opens com.inventory.ims to javafx.fxml;
    exports com.inventory.ims;
}