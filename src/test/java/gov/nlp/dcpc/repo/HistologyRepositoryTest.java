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
    @Autowired
    //HistologyRepositoryImpl histologyRepo;

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
        validateAndPrint(list);
    }

    @Test
    public void findByName() {
        List<Histology> list = histologyRepository.findByNameContaining("VERRUCOUS");
        validateAndPrint(list);
    }



    @Test
    public void findBySiteName() {
       List<Histology> list = histologyRepository.findBySitesName("PLACENTA");
       validateAndPrint(list);
    }

    @Test
    public void findBySiteCode() {
        List<Histology> list = histologyRepository.findBySitesCode("C58");
        validateAndPrint(list);
    }

    @Test
    public void findBySynonym() {
        List<Histology> list = histologyRepository.findByNameContainingOrSynonymsNameContaining("ROUND");
        validateAndPrint(list);
    }

    @Test
    public void findByCriteria() {
        List<Histology> list = histologyRepository.findMatchingHistologies("breast", "carcinoma", "paget");
        validateAndPrint(list);
    }
    @Test
    public void findByCriteriaNoMatches() {
        List<Histology> list = histologyRepository.findMatchingHistologies("lip");
        assert list == null || list.size() == 0;
    }



    private void validateAndPrint(List<Histology> list) {
        assert list != null && list.size() > 0;
        System.out.println("list.size() = " + list.size());
        for (Histology h: list) {
            System.out.println("h = " + h);
        }
    }

}