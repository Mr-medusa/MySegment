package red.medusa.service.entity;

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
    TYPESCRIPT("ts"),
    CSS("css"),
    SASS("scss"),
    VUE("vue"),
    KOTLIN("kt"),
    GO("go"),
    MARKDOWN("md"),
    SHELL("sh"),
    C("c"),
    DOCKERFILE("Dockerfile");
    public String value;

    LangType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
