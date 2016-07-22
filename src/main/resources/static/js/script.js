$(document).ready(function(){
  var samples = [
    {
      query:   'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
	       'PREFIX dcterms: <http://purl.org/dc/terms/>\n'+
	       'PREFIX sio: <http://semanticscience.org/resource/>\n' +
	       'PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\n'+

	       'SELECT DISTINCT ?gene ?disease  WHERE {\n'+ 
	       '?gda sio:SIO_000628 ?disease,?gene .\n'+
	       '?gene rdf:type ncit:C16612 .\n'+
		'?disease dcterms:title ?diseaseName . FILTER (str(?diseaseName) = "Obesity")\n'+
		'?disease rdf:type ncit:C7057 } limit 20',
        
	pattern: '?disease SubClassOf(has-causation some ?disease)',
        endpoint: 'http://rdf.disgenet.org/sparql/'
    },
    {
      query:  'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n' +
		'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
		'PREFIX dcterms: <http://purl.org/dc/terms/>\n'+
		'PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n'+
		'PREFIX sio: <http://semanticscience.org/resource/>\n'+
		'PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\n'+

              'SELECT DISTINCT ?disease ?phenotype WHERE {\n' +
              '?gda sio:SIO_000628 ?disease,?gene .\n' +
              '?disease rdf:type ncit:C7057 .\n' +
              '?disease skos:exactMatch ?test .\n' +
              '?test sio:SIO_001279 ?phenotype .\n' +
              '?disease dcterms:title ?diseaseName . FILTER (str(?diseaseName) = "Obesity")\n' +
              '} limit 20',
      pattern: '?disease SubClassOf(has-phenotype some ?phenotype)',
      endpoint: 'http://rdf.disgenet.org/sparql/'
    }
  ];


  var queries = Array();
  function display(){
    var queryCounter = $('#query-counter');
    var stepIndex1 = $('#step-1');
    var stepIndex2 = $('#step-2');
    var saveBtn = $('#save');
    saveBtn.prop('disabled', true);

    var query = '';
    var endpoint = '';
    var pattern = '';
    var count = queries.length;
    if (count > 0) {
      query = queries[0]['query'];
      endpoint = queries[0]['endpoint'];
      pattern = queries[0]['pattern'];

      queryCounter.text( count + ' ' +  (count == 1 ? 'query' : 'queries') + ' submitted.');
      stepIndex1.addClass('completed');
      stepIndex2.removeClass('completed');
    } else {
      queryCounter.text('No queries are submitted.');
      stepIndex1.removeClass('completed');
      stepIndex2.removeClass('completed');
      saveBtn.prop('disabled', true);
    }

    for (var i = 1; i < queries.length; i++) {
      query += ':::\n' + queries[i]['query'];
      endpoint += ':::\n' + queries[i]['endpoint'];
      pattern += ':::\n' + queries[i]['pattern'];
    }
    $('#hquery').val(query);
    $('#hendpoint').val(endpoint);
    $('#hpattern').val(pattern);

  }

  $('#submit').click(function(e){
    var query = $('#query').val();
    var endpoint = $('#endpoint').val();
    var pattern = $('#pattern').val();
    if (!query || !endpoint || !pattern) {
      alert('All fields are required!');
      return false;
    }
    queries.push({
      'query': query,
      'endpoint': endpoint,
      'pattern': pattern
    });
    $('#query').val('');
    $('#endpoint').val('');
    $('#pattern').val('');
    display();
    return false;
  });
  $('#reset').click(function(e){
    e.preventDefault();
    queries = [];
    $('#query').val('');
    $('#endpoint').val('');
    $('#pattern').val('');
    display();
    return false;
  });

  $('#save').click(function(e){
    if (queries.length > 0) {
      $('#step-2').addClass('completed');
    }
  });

  $( "#convert-form select" ).change(function() {
    if (queries.length > 0) {
      $('#save').prop('disabled', false);
    }
  });


  function populateSample(sampleIndex) {
    if (sampleIndex >=0 & sampleIndex <= samples.length) {
      var sample = samples[sampleIndex];
      $('#query').val(sample.query);
      $('#endpoint').val(sample.endpoint);
      $('#pattern').val(sample.pattern);
    }
  }

  $('#btn-sample1').click(function(e){
    e.preventDefault();
    populateSample(0);
  });
  
  $('#btn-sample2').click(function(e){
    e.preventDefault();
    populateSample(1);
  });

  display();

});
