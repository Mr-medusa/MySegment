package red.medusa.service.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
public class Audit {

    @PrePersist
    public void setCreateTime(BaseEntity o) {
        o.setCreateTime( new Date() );
        o.setUpdateTime( new Date() );
    }

    @PreUpdate
    public void setLastUpdateTime(BaseEntity o){
        o.setUpdateTime(new Date());
    }


}
