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
            @RequestParam(value="endpoint", required=true) String endpoint,
            @RequestParam(value="query", required=true) String query,
            @RequestParam(value="pattern", required=true) String pattern,
            HttpServletResponse response) {
        response.setContentType("application/owl");
        response.setHeader("Content-disposition", "attachment; filename=ontology.owl");
        try {
            OutputStream os = response.getOutputStream();
            OWLOntology ontology = manager.createOntology();


            String sq2= "PREFIX up:<http://purl.uniprot.org/core/>\n"+
                    "PREFIX keywords:<http://purl.uniprot.org/keywords/>\n"+
                    "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n"+
                    "SELECT DISTINCT ?protein ?db ?link\n"+
                    "WHERE{\n"+
                    "?protein a up:Protein .\n"+
                    "?protein up:classifiedWith keywords:3 .\n"+
                    "?protein rdfs:seeAlso ?link .\n"+
                    "?link up:database ?db .\n"+
                    "?db up:category '3D structure databases'} LIMIT 10\n";

            String relationalPattern = "(has-relation1 some ?protein) SubClassOf(has-relation some ?db or ?db)";
            String sparqlEndpoint = "http://sparql.uniprot.org/";

            // upon submmit
            ResultSet results = this.sparql2OWL.getSparqlResults(query, endpoint);
            OWLOntology newOntology = this.sparql2OWL.createOntologyFromSparql(results, pattern, ontology, dataFactory, manager, IRI);
            manager.saveOntology(ontology, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
