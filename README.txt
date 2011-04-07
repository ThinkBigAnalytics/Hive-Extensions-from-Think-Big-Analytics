This is a collection of extensions for Hive.

These three UDFs allow more complicated JSON operations, e.g.,

create temporary function get_json_full as 'com.thinkbiganalytics.hive.udf.UDFFullJsonPath';
create temporary function array_offset as 'com.thinkbiganalytics.hive.udf.UDFArrayOffset';
create temporary function array_deref as 'com.thinkbiganalytics.hive.udf.UDFArrayDeref';

e.g.,

select array_deref(get_json_full(json, '$.*.val'),
  array_offset(get_json_full(json, '$.*.key'), 'two')

run on

{ 'xyz' : { 'key' : 'one', 'val' : '11' }, {'key' : 'two', 'val', 'yes'} }  

will return "yes"

You can also use more advanced JSONPath operations like:

get_json_full(json, '$.*[?(@.KEY=two)].val')[0]

which also returns "yes"