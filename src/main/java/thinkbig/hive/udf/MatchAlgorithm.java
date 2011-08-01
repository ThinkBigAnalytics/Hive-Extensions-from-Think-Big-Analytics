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

// for any new matching algorithm implementation, 
// just extend from the MatchAlgorithm class and
// implement the match and countMatch methods
// strategy design pattern, abstract the algorithm implementation
public abstract class MatchAlgorithm { 
  public abstract boolean match(String text, String pattern);
  public abstract int countMatch(String text, String pattern);
}
