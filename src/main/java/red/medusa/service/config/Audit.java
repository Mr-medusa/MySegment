package red.medusa.service.config;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Segment;

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
    public void setLastUpdate(Segment o) {
        o.setUpdateTime(new Date());
    }

    @PrePersist
    public void setCreateTime(Segment o) {
        o.setUpdateTime(new Date());
        o.setCreateTime(new Date());
    }
}