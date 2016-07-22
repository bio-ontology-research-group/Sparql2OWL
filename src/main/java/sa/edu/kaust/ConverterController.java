package sa.edu.kaust;

import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import javax.servlet.http.*;
import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private final static String iri = "www.somewhere.net/#";
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

        // Generate file name
        SimpleDateFormat format = new SimpleDateFormat();
        format = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String filename = "ontology_" + format.format(new Date()) + ".owl";


        response.setContentType("application/rdf+xml");
        response.setHeader("Content-disposition", "attachment; filename=" + filename);
        try {
            OutputStream os = response.getOutputStream();
            OWLOntology ontology = manager.createOntology();

            // upon submmit
            String separator = ":::[\\r\\n]+";
            String[] endpoints = endpoint.split(separator);
            String[] queries = query.split(separator);
            String[] patterns = pattern.split(separator);
            for (int i = 0; i < queries.length; i++) {
                ResultSet results = this.sparql2OWL.getSparqlResults(queries[i], endpoints[i]);
                OWLOntology newOntology = this.sparql2OWL.createOntologyFromSparql(results, patterns[i], ontology, dataFactory, manager, iri);
            }
            String[] imports = refOnts.split(",");
            for (String import1: imports) {
                OWLImportsDeclaration imp = manager.getOWLDataFactory().getOWLImportsDeclaration(IRI.create(import1));
                manager.applyChange(new AddImport(ontology, imp));
            }
            manager.saveOntology(ontology, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
