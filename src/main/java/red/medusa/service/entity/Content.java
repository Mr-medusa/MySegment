package red.medusa.service.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author huguanghui
 * @since 2020/12/12 周六
 */
@Data
@Entity
@ToString
@DynamicUpdate
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=true)
public class Content extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LangType langType = LangType.JAVA;

    @Column(length = 8000)
    private String content;

    private String title;

    @Transient
    private int index = -1;
}
