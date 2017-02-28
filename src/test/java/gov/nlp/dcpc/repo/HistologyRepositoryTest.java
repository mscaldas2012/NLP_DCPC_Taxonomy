package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.collection.Iterator;
import scala.collection.mutable.ListBuffer;
import util.CSVReader;

import java.util.ArrayList;
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
    public void findByMatchingHistologyTerms() {
        List<Histology> list = histologyRepository.findMatchingHistologies("hidroadenoma");
        validateAndPrint(list);
    }
    @Test
    public void findByCriteriaNoMatches() {
        List<Histology> list = histologyRepository.findMatchingHistologies("lip");
        assert list == null || list.size() == 0;
    }


    @Test
    public void findTermsFromCDCDump() {
        CSVReader reader = new CSVReader();
        ListBuffer<String[]> lines = reader.readFile("src/test/resources/CDCDumpFindings.csv", " ");
        Iterator it = lines.iterator();
        while (it.hasNext()) {
            String[] row = (String[]) it.next();
            List validValues = new ArrayList();
            String allValues = "";
            for (String s: row) {
                if (!s.contains("?")) {
                    if (s.contains("'")) {
                        s = s.substring(0, s.indexOf("'") - 1);
                    }
                    allValues += s;
                    validValues.add(s);
                }
            }
            System.out.println(allValues);
            String[] a = new String[1];
            String[] validArray = (String[]) validValues.toArray(a);
            try {
                List<Histology> list = histologyRepository.findMatchingHistologies(validArray);
                for (Histology h : list) {
                    System.out.println("\th = " + h.getCode());
                }
            } catch (Exception e) {
                System.out.println("Unable to process " + allValues);
            }
        }

    }


    private void validateAndPrint(List<Histology> list) {
        assert list != null && list.size() > 0;
        System.out.println("list.size() = " + list.size());
        for (Histology h: list) {
            System.out.println("h = " + h);
        }
    }

}