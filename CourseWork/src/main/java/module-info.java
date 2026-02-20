module com.example.courseprifs {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.naming;
    requires mysql.connector.j;
    requires jakarta.persistence;
//    Optional thingies
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jbcrypt;
    requires javafx.graphics;

    opens com.example.courseprifs to javafx.fxml, org.hibernate.orm.core, jakarta.persistence;
    exports com.example.courseprifs;
    opens com.example.courseprifs.fxControllers to javafx.fxml;
    exports com.example.courseprifs.fxControllers;
    opens com.example.courseprifs.model to org.hibernate.orm.core;
    exports com.example.courseprifs.model;
    exports com.example.courseprifs.utils;
    opens com.example.courseprifs.utils to javafx.fxml;
}