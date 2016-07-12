$(document).ready(function(){
  var queries = Array();
  function display(){
    var tBody = $('#tBody');
    tBody.html('');
    var content = '';
    for(var i = 0; i < queries.length; i++) {
      content += '<tr>';
      content += '<td>' + queries[i]['query'] + '</td>';
      content += '<td>' + queries[i]['endpoint'] + '</td>';
      content += '<td>' + queries[i]['pattern'] + '</td>';
      content += '</tr>';
    }
    tBody.html(content);
    var query = '';
    var endpoint = '';
    var pattern = '';
    if (queries.length > 0) {
      query = queries[0]['query'];
      endpoint = queries[0]['endpoint'];
      pattern = queries[0]['pattern'];
    }
    for (var i = 1; i < queries.length; i++) {
      query += ':::' + queries[i]['query'];
      endpoint += ':::' + queries[i]['endpoint'];
      pattern += ':::' + queries[i]['pattern'];
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
    queries.clear();
    display();
    return false;
  });
});
