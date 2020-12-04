package red.medusa.service.config;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.BaseEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Slf4j
public class Audit {
    @PreUpdate
    public void setLastUpdate(BaseEntity o) {
        o.setUpdateTime(new Date());
    }

    @PrePersist
    public void setCreateTime(BaseEntity o) {
        o.setUpdateTime(new Date());
        o.setCreateTime(new Date());
    }
}