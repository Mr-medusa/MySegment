package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Data
@Entity
@DynamicUpdate
@Accessors(chain = true)
@SuppressWarnings("all")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Segment extends BaseEntity {
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(length = 3000)
    private String dependence;
    @Column(length = 8000)
    private String content;

    @Enumerated(EnumType.STRING)
    private LangType langDep = LangType.COMBOBOX_FIRST_SELECT;

    @Enumerated(EnumType.STRING)
    private LangType langContent = LangType.COMBOBOX_FIRST_SELECT;

    @JoinColumn(name="module_id",referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Module module;

    @JoinColumn(name="version_id",referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Version version;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id asc")
    private Set<Url> urls;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id asc")
    private Set<Img> imgs;

}




















