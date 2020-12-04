package red.medusa.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import red.medusa.service.entity.*;
import red.medusa.service.utils.HUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Slf4j
@SuppressWarnings("all")
public class CurdTest {
    private static final EntityManager entityManager = HUtils.createEntityManager();

    @Before
    public void before() {
        entityManager.getTransaction().begin();
    }

    @Test
    public void createTest() throws IOException {

        Url url01 = new Url().setUrl("https://spring.io/");
        Url url02 = new Url().setUrl("https://docs.jboss.org");

        Module module01 = new Module().setName("Spring");
        Version version01 = new Version().setModule(module01).setName("v1.0");
        Version version02 = new Version().setModule(module01).setName("v2.0");

        module01.setVersions(Arrays.asList(version01, version02));

        Segment segment01 = new Segment()
                .setName("A").setDescription("aaa").setContent("aaaaa")
                .setModule(module01)
                .setVersion(version02)
                .setDependence("setDependence");

        // 02
        Module module02 = new Module().setName("Hibernate");
        Version version03 = new Version().setModule(module02).setName("v3.0");
        Version version04 = new Version().setModule(module02).setName("v4.0");

        Url url03 = new Url().setUrl("https://spring.io/");
        Url url04 = new Url().setUrl("https://docs.jboss.org");

        Segment segment02 = new Segment()
                .setName("B")
                .setModule(module02)
                .setVersion(version03)
                .setDescription("bbb")
                .setContent("bbbbb")
                .setDependence("Dep");

        entityManager.persist(version01);
        entityManager.persist(version02);
        entityManager.persist(version03);
        entityManager.persist(version04);

        entityManager.persist(module01);
        entityManager.persist(module02);

        entityManager.persist(segment01);
        entityManager.persist(segment02);
    }

    @Test
    public void selectWithKeywordTest() throws IOException {
        String keyword = "s";
        keyword = keyword.trim();

        Query segmentQuery = entityManager.createNativeQuery(
                "select s.name,s.description,m.name as mName,s.id from " +
                "module m left join version v on m.id=v.module_id left join segment s on s.module_id=m.id " +
                "where m.id=?1 and v.id=?2 order by m.name,s.create_time desc"
        );
        segmentQuery.setParameter(1,2L);
        segmentQuery.setParameter(2,1L);
        List<Object[]> segments = segmentQuery.getResultList();
        for (Object[] segment : segments) {
            System.out.println(Arrays.toString(segment));
        }
    }

    @Test
    public void updateTest() throws IOException {
        Segment segment = entityManager.find(Segment.class, 1);
        segment.setName("HHH");
        segment.setDescription("Dep");
        entityManager.merge(segment);
    }

    @Test
    public void deleteTest() throws IOException {
        Segment segment = entityManager.find(Segment.class, 1);
        entityManager.remove(segment);
    }

    @Test
    public void findModuleTest() {
        List<Module> modules = entityManager.createQuery("from Module").getResultList();
        for (Module module : modules) {
            System.out.println(module);
            for (Version version : module.getVersions()) {
                System.out.println(version);
            }
        }
    }

    @Test
    public void dynamicUpdateTest(){
        Segment segment = entityManager.find(Segment.class, 1L);
        segment.setName("HHHH");
    }

    @Test
    public void selectPageTest() throws IOException {
        int pageSize = 10;
        int currentPage = 1;

        Query segmentQuery = entityManager.createQuery("from Segment");
        List<Segment> segments = segmentQuery
                .setFirstResult((currentPage - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
//        segments.forEach(System.out::println);
        for (Segment segment : segments) {
            System.out.println(segment.getUrls());
        }
        System.out.println("-- Page info :");
        long totalCount = (long) entityManager.createQuery("select count(id) from Segment").getSingleResult();
        int pageCount = (int) (totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1);
        System.out.println("当前页码: " + currentPage);
        System.out.println("每页大小:" + pageSize);
        System.out.println("总页数: " + pageCount);
        System.out.println("总条数: " + totalCount);
    }

    @After
    public void after() {
        entityManager.getTransaction().commit();
        entityManager.close();
    }


}
