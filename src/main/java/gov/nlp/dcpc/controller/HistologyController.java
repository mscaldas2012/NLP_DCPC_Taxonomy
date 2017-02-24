package gov.nlp.dcpc.controller;

import gov.nlp.dcpc.model.Histology;
import gov.nlp.dcpc.repo.HistologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by marcelo on 2/24/17.
 */
@Controller
@RequestMapping("/histologyMatch")
public class HistologyController {
    @Autowired
    HistologyRepository histologyRepository;

    @RequestMapping
    @ResponseBody
    public List<Histology> findMatchingHistologies(@RequestParam("values") String... values) {
        return histologyRepository.findMatchingHistologies(values);
    }
}
