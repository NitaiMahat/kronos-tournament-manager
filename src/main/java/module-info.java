module edu.augustana.csc305.project {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires org.jooq;
    requires okhttp3;
    requires retrofit2;
    requires retrofit2.converter.gson;
    requires annotations;

    exports edu.augustana.csc305.project;

    opens edu.augustana.csc305.project.model.api;
    opens edu.augustana.csc305.project.model.domain;

    opens edu.augustana.csc305.kronos.db.tables.records to org.jooq;
}