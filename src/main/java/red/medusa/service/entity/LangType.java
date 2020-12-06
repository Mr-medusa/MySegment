package red.medusa.service.entity;

import red.medusa.ui.SegmentAddOrEdit;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
public enum LangType {
    JAVA("java"),
    JAVASCRIPT("js"),
    XML("xml"),
    YAML("yaml"),
    PROPERTIES("properties"),
    TXT("txt"),
    SQL("sql"),
    HTML("html"),
    KOTLIN("kt"),
    GO("go"),
    C("c"),
    CSS("css"),
    MARKDOWN("md"),
    SHELL("sh"),
    DOCKERFILE("Dockerfile"),
    COMBOBOX_FIRST_SELECT(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT);
    public String value;

    LangType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if(this == LangType.COMBOBOX_FIRST_SELECT)
            return SegmentAddOrEdit.COMBOBOX_FIRST_SELECT;
        return this.name();
    }
}
