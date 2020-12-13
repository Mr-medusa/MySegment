package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Module extends BaseEntity {

    private String name;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "module")
    @OrderBy("id asc")
    private List<Category> categories = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
