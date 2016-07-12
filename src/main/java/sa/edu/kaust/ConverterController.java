package sa.edu.kaust;

import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import javax.servlet.http.*;
import javax.annotation.PostConstruct;
import java.io.*;


import org.apache.jena.query.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.apache.jena.query.*;
import sa.edu.kaust.Sparql2OWL;


@Controller
public class ConverterController {
    private final static String IRI = "www.somewhere.net/#";
    OWLDataFactory dataFactory;
    OWLOntologyManager manager;
    Sparql2OWL sparql2OWL;

    @PostConstruct
    public void init() {
        this.dataFactory = OWLManager.getOWLDataFactory();
        this.manager = OWLManager.createOWLOntologyManager();
        this.sparql2OWL = new Sparql2OWL();
    }

    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    public void convert(
            @RequestParam(value="hendpoint", required=true) String endpoint,
            @RequestParam(value="hquery", required=true) String query,
            @RequestParam(value="hpattern", required=true) String pattern,
            @RequestParam(value="refOntology", required=true) String refOnts,
            HttpServletResponse response) {
        response.setContentType("application/rdf+xml");
        response.setHeader("Content-disposition", "attachment; filename=ontology.owl");
        try {
            OutputStream os = response.getOutputStream();
            OWLOntology ontology = manager.createOntology();

            // upon submmit
            String[] enpoints = endpoint.split(":::");
            String[] queries = query.split(":::");
            String[] patterns = pattern.split(":::");
            for (int i = 0; i < queries.length; i++) {
                ResultSet results = this.sparql2OWL.getSparqlResults(queries[i], endpoints[i]);
                OWLOntology newOntology = this.sparql2OWL.createOntologyFromSparql(results, patterns[i], ontology, dataFactory, manager, IRI);
            }
            String[] imports = refOnts.split(",");
            for (String import: imports) {
                OWLImportsDeclaration imp = manager.getOWLDataFactory().getOWLImportsDeclaration(IRI.create(import));
                manager.applyChange(new AddImport(ontology, imp));
            }
            manager.saveOntology(ontology, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
