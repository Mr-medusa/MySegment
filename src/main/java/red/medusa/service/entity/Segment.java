package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import red.medusa.service.config.Audit;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@SuppressWarnings("all")
@Data
@Entity
@ToString
@DynamicUpdate
@Accessors(chain = true)
@EntityListeners(Audit.class)
@EqualsAndHashCode(callSuper=true)
public class Segment extends BaseEntity{

    private String name;
    @Column(length = 1000)
    private String description;

    @JoinColumn(name="module_id",referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Module module;

    @JoinColumn(name="category_id",referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Category category;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id asc")
    private Set<Url> urls;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id asc")
    private Set<Img> imgs;

    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("id asc")
    @NotFound(action= NotFoundAction.IGNORE)
    private Set<Content> contents = new LinkedHashSet<>();

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updateTime;
}




















