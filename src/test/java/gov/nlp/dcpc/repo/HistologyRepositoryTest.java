package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by marcelo on 2/18/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HistologyRepositoryTest {
    @Autowired
    HistologyRepository histologyRepository;

    @Test
    public void findByCode() {
        Histology h = histologyRepository.findByCode("8000/3");
        System.out.println("h = " + h);
        assert h != null;
        assert h.getCode().equals("8000/3");
    }

    @Test
    public void findByHistology() {
        List<Histology> list = histologyRepository.findByHistology(8000);
        assert list != null && list.size() > 0;
        for (Histology h: list) {
            System.out.println("h = " + h.getName());
        }
    }

    @Test
    public void findByName() {
        List<Histology> list = histologyRepository.findByNameContainingIgnoreCase("Tumor");
        assert list != null && list.size() > 0;
        for (Histology h: list) {
            System.out.println("h = " + h);
        }

    }

}