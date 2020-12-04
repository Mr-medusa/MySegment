package red.medusa.test;

import org.junit.Test;
import red.medusa.service.entity.Module;
import red.medusa.service.entity.Segment;
import red.medusa.service.service.BaseEntityService;
import red.medusa.service.service.SegmentEntityService;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
public class BaseEntityServiceTest {

    @Test
    public void findTest()  {
        BaseEntityService baseEntityService = SegmentEntityService.getInstance();
        baseEntityService.startService();
        Segment segment = baseEntityService.find(1L, Segment.class);
        System.out.println(segment);
        baseEntityService.finishService();
    }
    @Test
    public void listTest() {
        BaseEntityService baseEntityService = SegmentEntityService.getInstance();
        baseEntityService.startService();

        List<Module> list = baseEntityService.list(Module.class);
        System.out.println(list);

        baseEntityService.finishService();
    }

    @Test
    public void mergeTest() {
        BaseEntityService baseEntityService = SegmentEntityService.getInstance();
        baseEntityService.startService();

        Module module = (Module) new Module().setName("Hello").setId(3L);
        Module module1 = baseEntityService.merge(module);
        System.out.println(module1);

        baseEntityService.finishService();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void listByCustomTest() {
        BaseEntityService baseEntityService = SegmentEntityService.getInstance();
        baseEntityService.startService();

        String sql = "from Module where name = ?1 order by createTime";
        Function<EntityManager,List<Module>> function = (e)-> e.createQuery(sql)
                .setParameter(1,"module").getResultList();
        List<Module> modules = baseEntityService.list(function);

        System.out.println(modules);

        baseEntityService.finishService();
    }
}
