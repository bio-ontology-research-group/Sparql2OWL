# SPARQL2OWL

SPARQL2OWL is a tool to convert some parts of RDF knowledge into
OWL so that it can be used together with background knowledge from
ontologies, or alone, for querying and quality control.

## This requires as input the following:
1. SPARQL query.
2. SPARQL endpoint.
3. Relational pattern in Manchester Syntax


In this tool, the user can ask multiple SPARQL queries and merge them with background ontologies, 
which are used in within the RDF data accessible through the SPRQL endpoint.
Examples of SPARQL queries on DisGeNET

```
SELECT DISTINCT ?disease ?phenotype WHERE {
?gda sio:SIO_000628 ?disease,?gene .
?disease rdf:type ncit:C7057 .
?disease skos:exactMatch ?test .
?test sio:SIO_001279 ?phenotype .
} limit 10

```

An example of a suitable relational pattern would be:

```
?X subClassOf (has-phenotype some ?Y)
```

Using the resulting ontology, several DL queries can be asked by utilizing background ontologies such as Human Phenotype ontologies, Gene ontology and so on:
```
has-phenotype some ('Intellectual disability' and 'has part' some ('has modifier' some 'mild intensity' and 'has modifier' some abnormal))
```
The output would be the diseases that satisfies this query 

The tool has support for multiple queries with n variabes, and multiple relational patterns.
