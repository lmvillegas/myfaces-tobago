package org.apache.myfaces.tobago.example.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SelectBooleanCheckboxBean {
  
  private boolean value00 = true;
  private boolean value01 = true;
  private boolean value02 = true;
  private boolean value10 = true;
  private boolean value11 = true;
  private boolean value12 = true;

  public boolean isValue0() {
    return value00;
  }

  public void setValue0(boolean value00) {
    this.value00 = value00;
  }

  public boolean isValue1() {
    return value01;
  }

  public void setValue1(boolean value01) {
    this.value01 = value01;
  }

  public boolean isValue2() {
    return value02;
  }

  public void setValue2(boolean value02) {
    this.value02 = value02;
  }

  public boolean isRequired0() {
    return value10;
  }

  public void setRequired0(boolean value10) {
    this.value10 = value10;
  }

  public boolean isRequired1() {
    return value11;
  }

  public void setRequired1(boolean value11) {
    this.value11 = value11;
  }

  public boolean isRequired2() {
    return value12;
  }

  public void setRequired2(boolean value12) {
    this.value12 = value12;
  }
}
