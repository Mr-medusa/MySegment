package red.medusa.service.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * @author huguanghui
 * @since 2020/09/01 星期二
 */
@Slf4j
public class MyPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        String text = warp(name.getText());
        return super.toPhysicalColumnName(new Identifier(text, name.isQuoted()), context);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String text = warp(name.getText());
        return super.toPhysicalColumnName(new Identifier(text, name.isQuoted()), context);
    }

    /*
     * 名字里包含有大写字符则在前面加_并转小写返回
     */
    public static String warp(String text) {
        text = text.replaceAll("([A-Z])", "_$1").toLowerCase();
        if (text.charAt(0) == '_') {
            text = text.replaceFirst("_", "");
        }
        return text;
    }
}


























