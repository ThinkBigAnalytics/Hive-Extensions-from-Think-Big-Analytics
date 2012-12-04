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
public class TestMd5UDF extends TestCase {

    String text;

    final String pattern = "There once was a king named David.";
    final String pattern2 = "There once was a king named David.";
    final String pattern3 = "There once was a king named Midas.";

    @Before
    public void setup() {
    }

    @Test
    public void test() {
        Md5 m = new Md5();
        assertNotNull( m.evaluate( new Text( pattern ) ) );
        assertEquals( 32, m.evaluate( new Text( pattern ) ).getLength() );
        assertEquals( m.evaluate( new Text( pattern ) ).toString(), m.evaluate( new Text( pattern2 ) ).toString() );
    }

    @Test
    public void testDifferentPatterns() {
        Md5 m = new Md5();
        assertFalse( m.evaluate( new Text( pattern ) ).toString().equals( m.evaluate( new Text( pattern3 ) ).toString() ) );
        assertEquals( 32, m.evaluate( new Text( pattern3 ) ).getLength() );
    }

    @Test
    public void testDisplayString() {
        Md5 m = new Md5();
        assertNotNull( m.getDisplayString() );
    }

}