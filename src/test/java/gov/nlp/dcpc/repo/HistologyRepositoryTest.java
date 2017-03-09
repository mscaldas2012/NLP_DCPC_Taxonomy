package gov.nlp.dcpc.repo;

import gov.nlp.dcpc.model.Histology;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.collection.Iterator;
import scala.collection.mutable.ListBuffer;
import util.BagOfWords;
import util.CSVReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    public void findTermsFromCDCDump() throws IOException {
        CSVReader reader = new CSVReader();
        BagOfWords bofw = new BagOfWords();
        scala.collection.immutable.ListMap<String, Object> words = bofw.createBagOfWords("data/src/resources/histology.txt");
        ListBuffer<String[]> lines = reader.readFile("src/test/resources/CDCDumpCleaned.txt", " ");

        BufferedWriter bw = new BufferedWriter(new FileWriter("CDCDumpOutput-1.csv"));
        //write the header:
        bw.write("Term|NumberOfMatches|Matches|BagOfWordPresence\n");
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
                    s = s.replaceAll(",","").replaceAll("\\(", "").replaceAll("\\)","");
                    allValues += s + " ";
                    validValues.add(s);
                }
            }
            String[] a = new String[1];
            String[] validArray = (String[]) validValues.toArray(a);
            String bagOfWordsPresence= "";
            try {
                List<Histology> list = histologyRepository.findMatchingHistologies(validArray);
                if (list.size() == 0) {
                    bagOfWordsPresence = "";
                    //Check if any of the words are in the bag of words:
                    for (String s: validArray) {
                        System.out.println("checking for = " + s);
                        if (words.contains(s.toUpperCase().trim())) {
                            bagOfWordsPresence += s + ", ";
                        }
                    }
                    bw.write(allValues + "|" + list.size() + "|None|" + bagOfWordsPresence + "\n");
                } else {
                    String content = allValues + "|" + list.size() + "|";
                    for (Histology h : list) {
                        content += h.getCode() + ", ";
                    }
                    bw.write(content + "|" + "\n");
                }
            } catch (Exception e) {
                bw.write(allValues + "|Error" );
                System.out.println("Unable to process " + allValues);
            }
        }
        bw.close();
    }


    private void validateAndPrint(List<Histology> list) {
        assert list != null && list.size() > 0;
        System.out.println("list.size() = " + list.size());
        for (Histology h: list) {
            System.out.println("h = " + h);
        }
    }

}