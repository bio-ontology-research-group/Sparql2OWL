package sa.edu.kaust;

import org.apache.jena.query.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.apache.jena.query.*;

import java.util.List;

/**
 * Created by mona on 15/06/16.
 */



public class Sparql2OWL {


    public void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException{

        final  OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        final  OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        final  String iri = "www.somewhere.net/#";
        final OWLOntology ontology = manager.createOntology();

        //while(submit){
        String sparqlQuery1= "PREFIX up:<http://purl.uniprot.org/core/> PREFIX keywords:<http://purl.uniprot.org/keywords/> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> 
        SELECT DISTINCT ?protein ?db ?link WHERE{ ?protein a up:Protein . 
        ?protein up:classifiedWith keywords:3 . ?protein rdfs:seeAlso ?link . 
        ?link up:database ?db . ?db up:category '3D structure databases'} LIMIT 10";

        String relationalPattern1 = "(has-relation1 some ?protein) SubClassOf(has-relation some ?db or ?db)";
        String sparqlEndpoint1= "http://sparql.uniprot.org/";


        String sparqlQuery2 = "PREFIX GO: <http://purl.uniprot.org/go/>
                PREFIX taxon:<http://purl.uniprot.org/taxonomy/>
                PREFIX up: <http://purl.uniprot.org/core/>
                PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
                SELECT DISTINCT ?protein ?ontid WHERE {
                ?protein up:classifiedWith ?ontid .
                FILTER regex(str(?ontid),\"GO+\")} LIMIT 10";
        
        String relationalPattern2 = "?protein SubClassOf(has-relation some ?ontid)"
        results = getSparqlResults(sq1, sparqlEndpoint);
        OWLOntology newOntology = createOntologyFromSparql(results, relationalPattern1, ontology,dataFactory, manager,iri);

        OWLOntologyMerger merger = new OWLOntologyMerger(manager);

        ResultSet results = getSparqlResults(sq2, sparqlEndpoint);
        newOntology = createOntologyFromSparql(results, relationalPattern2,ontology, dataFactory, manager, iri);

        //}

         //before save, loop over all reference ontologies from index page, this is exmaple
        OWLImportsDeclaration imports = manager.getOWLDataFactory().getOWLImportsDeclaration(IRI.create("http://purl.obolibrary.org/obo/hp.owl"));
        manager.applyChange(new AddImport(ontology, imports));

        
        //upon save
        manager.saveOntology(ontology,System.out);


    }

    public boolean checkInput(List<String> list, String[] strArr) {
        int countVar = 0;
        int countMatch = 0;
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].startsWith("?")) {
                countVar++;
            }
            if (list.contains(strArr[i].replace("?", ""))) {
                countMatch++;
            }
        }

        if (countMatch == countVar && countVar <= list.size())
            return true;
        else
            return false;
    }


    public ResultSet getSparqlResults(String sparqlQuery, String sparqlEndpoint) {

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution queryExec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
        ResultSet results = queryExec.execSelect();
        ResultSet results2 = queryExec.execSelect();
        ResultSetFormatter.out(results2);

        return results;

    }


    public OWLOntology createOntologyFromSparql(ResultSet results, String input, OWLOntology ontology, OWLDataFactory dataFactory, OWLOntologyManager manager, String iri) {


        List<String> classList = results.getResultVars();
        System.out.println(classList.toString());


        String orig_input = new String(input);
        String input1 = input.replaceAll("\\(", " ").replaceAll("\\)", " ");
        System.out.println(input1);
        String[] strArr = input1.split(" ");

        System.out.println(checkInput(classList, strArr));

        //check #input and match with SPARQL query vars
        if (checkInput(classList, strArr)) {
            while (results.hasNext()) {


                Provider shortFormProvider = new Provider();
                OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

                QuerySolution querySolution = results.next();
                input = input.replaceAll("\\?", "");

                for (int i = 0; i < strArr.length; i++) {
                    if (strArr[i].startsWith("?")) {

                        int ind = classList.indexOf(strArr[i].replace("?", ""));
                        String str = querySolution.get(classList.get(ind)).toString();
                        shortFormProvider.add(dataFactory.getOWLClass(IRI.create(str)));
                        String[] srcArr = str.split("/");
                        String var1 = srcArr[srcArr.length - 1];
                        input = input.replace(strArr[i].replace("?", ""), var1);

                    }
                    if (strArr[i].startsWith("has")) {
                        shortFormProvider.add(dataFactory.getOWLObjectProperty(IRI.create(iri + strArr[i])));
                    }
                }

                ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
                parser.setOWLEntityChecker(entityChecker);
                parser.setStringToParse(input);
                OWLAxiom axiom = parser.parseAxiom();
                manager.addAxiom(ontology, axiom);
                input = new String(orig_input);

            }

        } else {
            System.out.println("wrong input");

        }
        return ontology;
    }




}
