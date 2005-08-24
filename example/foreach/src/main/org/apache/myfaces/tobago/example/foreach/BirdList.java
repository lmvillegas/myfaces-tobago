package org.apache.myfaces.tobago.example.foreach;

import java.util.List;
import java.util.ArrayList;

/*
 * Copyright 2002-2005 atanion GmbH.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


public class BirdList {

  List birds = null;

  public BirdList() {

  }

  public List getBirds() {
    if (birds == null) {
      birds = new ArrayList();
      birds.add("Amsel");
      birds.add("Drossel");
      birds.add("Fink");
      birds.add("Star");
    }
    return birds;
  }
}
