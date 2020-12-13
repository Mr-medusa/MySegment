package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Data
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Url extends BaseEntity {
    private String url;
    @Override
    public String toString() {
        return url;
    }
}
