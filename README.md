# A collection of extensions for Hive.

## Sophisticated JSON operations
Currently, this package provides three UDFs that enable more sophisticated JSON operations, e.g.,

    create temporary function get_json_full as 'thinkbig.hive.udf.UDFFullJsonPath';
    create temporary function array_offset as 'thinkbig.hive.udf.UDFArrayOffset';
    create temporary function array_deref as 'thinkbig.hive.udf.UDFArrayDeref';

For example, for the following the `JSON` string:

    { 'xyz' : { 'key' : 'one', 'val' : '11' }, {'key' : 'two', 'val', 'yes'} }  

The following query will return "yes":

    SELECT array_deref(get_json_full(json, '$.*.val'),
      array_offset(get_json_full(json, '$.*.key'), 'two') ...;


You can also use more advanced JSONPath operations like the following (which also returns "yes"):

    get_json_full(json, '$.*[?(@.KEY=two)].val')[0]


## Rank, MD5, detunixtimestamp 

Example usage of the Rank function:
	add jar s3://tba.douglasmoore.code/hive-ext-thinkbig-2.0.jar;
	create temporary function rank as 'thinkbig.hive.udf.Rank';
	select hashtag, timebucket, rank(hashtag) rank from trending_count order by hashtag, timebucket;


And in an other example, the query below is an example of MD5 and detunixtimestamp:

	insert overwrite table CleanImpression
	  PARTITION (year, month, day)
	  select
	    coalesce(userCookieId,MD5(concat_ws(":",ipAddress,userAgent))),
	    pub,
	    page,
	    viewDateMillis,
	    advertiser,
	    year,
	    month,
	    day
	  from AdImpression
	  where (
	    detunixtimestamp(concat(year,month,day,'UTC'),'yyyyMMddzzz')>=${FROM_TS}-24*60*60 AND
	    detunixtimestamp(concat(year,month,day,'UTC'),'yyyyMMddzzz')<=${TO_TS}
	  );



## Version Notes

| Version | Hive and Hadoop Versions       | JAR File Name             |
| :-----: | :----------------------------- | :------------------------ |
| V2.0    | Hive v0.9.0 and Hadoop v1.0.3  | hive-ext-thinkbig-2.0.jar |
| V1.0    | Hive v0.7.0 and Hadoop v0.23.0 | hive-ext-thinkbig-1.0.jar |
