package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Data
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Img extends BaseEntity {
    @Lob
    private byte[] image;
    @Transient
    private String filename;
    @Override
    public String toString() {
        return "Img{" +
                "id = " + this.getId() +
                ",file size =" + getImage().length +
                '}';
    }
}
