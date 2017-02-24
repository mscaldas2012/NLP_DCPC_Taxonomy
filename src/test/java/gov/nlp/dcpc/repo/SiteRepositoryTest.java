package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Site;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by marcelo on 2/19/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SiteRepositoryTest {

    @Autowired
    private SiteRepository siteRepository;
    @Test
    public void findByCode() throws Exception {
        Site s = siteRepository.findByCode("C00");
        System.out.println("s = " + s);
        assert (s != null);
        assert s.getCode().equals("C00");
        assert s.getName().equals("LIP");

    }

    @Test
    public void findByMaincode() throws Exception {
        List<Site> list = siteRepository.findByMaincode("C00");
        assert list != null && list.size() > 0;
        for  (Site s: list) {
            System.out.println("list = " + s);
        }
    }

    @Test
    public void findByName() throws Exception {
        Site s = siteRepository.findByName("LIP");
        System.out.println("site = " + s);
        assert (s != null);
        assert s.getCode().equals("C00");
        assert s.getName().equals("LIP");
    }

    @Test
    public void findBySubcodeIsNull() throws Exception {
        List<Site> list = siteRepository.findBySubcodeIsNull();
        assert list != null && list.size() ==70 ;
        System.out.println("list.size() = " + list.size());
    }

    @Test
    public void findChildren() throws Exception {
        List<Site> list = siteRepository.findChildren("C00");
        assert list != null && list.size() > 0;
        for  (Site s: list) {
            System.out.println("list = " + s);
        }
    }

    @Test
    public void findAllSites() {
        Sort sort = new Sort(Sort.Direction.ASC, "name");

        Iterable<Site> list = siteRepository.findAll(sort);
        assert list != null ;
        for (Site site : list) {
            System.out.println("site = " + site);
        }
    }

}