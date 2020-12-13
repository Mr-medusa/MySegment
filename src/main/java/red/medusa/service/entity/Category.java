package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    private String name;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH})
    private Module module;

    @Override
    public String toString() {
        return name;
    }
}
