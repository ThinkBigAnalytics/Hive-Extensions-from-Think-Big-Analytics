# A collection of extensions for Hive.

Currently, this package provides three UDFs that enable more sophisticated JSON operations, e.g.,

    create temporary function get_json_full as 'com.thinkbiganalytics.hive.udf.UDFFullJsonPath';
    create temporary function array_offset as 'com.thinkbiganalytics.hive.udf.UDFArrayOffset';
    create temporary function array_deref as 'com.thinkbiganalytics.hive.udf.UDFArrayDeref';

For example, for the following the `JSON` string:

    { 'xyz' : { 'key' : 'one', 'val' : '11' }, {'key' : 'two', 'val', 'yes'} }  

The following query will return "yes":

    SELECT array_deref(get_json_full(json, '$.*.val'),
      array_offset(get_json_full(json, '$.*.key'), 'two') ...;


You can also use more advanced JSONPath operations like the following (which also returns "yes"):

    get_json_full(json, '$.*[?(@.KEY=two)].val')[0]

## Version Notes

| Version | Hive and Hadoop Versions       | JAR File Name             |
| :-----: | :----------------------------- | :------------------------ |
| V2.0    | Hive v0.9.0 and Hadoop v1.0.3  | hive-ext-thinkbig-2.0.jar |
| V1.0    | Hive v0.7.0 and Hadoop v0.23.0 | hive-ext-thinkbig-1.0.jar |