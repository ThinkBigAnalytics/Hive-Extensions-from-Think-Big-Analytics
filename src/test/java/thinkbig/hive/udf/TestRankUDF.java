/**
 * Licensed to the Apache Software Foundation (ASF) under one    
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2011 Think Big Analytics.
 */

package thinkbig.hive.udf;

import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * UnitTestcases for Hive Rank UDF
 * 
 */
public class TestRankUDF extends TestCase {

    String text;

    @Before
    public void setup() {
    }

    @Test
    public void test() {
        Rank r = new Rank();
        assertEquals( 0, r.evaluate( "name1" ) );
        assertEquals( 1, r.evaluate( "name1" ) );
        assertEquals( 2, r.evaluate( "name1" ) );
        assertEquals( 0, r.evaluate( "name2" ) );
        assertEquals( 1, r.evaluate( "name2" ) );
        assertEquals( 2, r.evaluate( "name2" ) );
    }

    @Test
    public void testNegative() {
        Rank r = new Rank();
        assertEquals( 0, r.evaluate( (Text) null ) );
        assertEquals( 0, r.evaluate( (Text) null ) );
        assertEquals( 0, r.evaluate( (Text) null ) );
    }

    @Test
    public void testLongNegative() {
        Rank r = new Rank();
        assertEquals( 0, r.evaluate( 34 ) );
        assertEquals( 1, r.evaluate( 34 ) );
        assertEquals( 2, r.evaluate( 34 ) );
    }

    @Test
    public void testLong() {
        Rank r = new Rank();
        assertEquals( 0, r.evaluate( new Long( 34 ) ) );
        assertEquals( 1, r.evaluate( new Long( 34 ) ) );
        assertEquals( 2, r.evaluate( new Long( 34 ) ) );
    }

    @Test
    public void testInteger() {
        Rank r = new Rank();
        assertEquals( 0, r.evaluate( new Integer( 35 ) ) );
        assertEquals( 1, r.evaluate( new Integer( 35 ) ) );
        assertEquals( 2, r.evaluate( new Integer( 35 ) ) );
    }

    @Test
    public void testDisplayString() {
        Rank r = new Rank();
        assertNotNull( r.getDisplayString() );
    }

}