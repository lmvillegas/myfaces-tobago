package org.apache.myfaces.tobago.example.data;

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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LocaleList {

  public static final List<LocaleEntry> DATA;

  public static final List<String> COUNTRY_LANGUAGE;

  static {
    List<LocaleEntry> init = new ArrayList<LocaleEntry>();
    for (Locale displayLocale : Locale.getAvailableLocales()) {
      for (Locale locale : Locale.getAvailableLocales()) {
        init.add(new LocaleEntry(locale, displayLocale));
      }
    }
    DATA = Collections.unmodifiableList(init);
  }

  static {
    Set<String> init = new HashSet<String>();
    for (LocaleEntry localeEntry : DATA) {
      if (StringUtils.isNotBlank(localeEntry.getCountry())
          && StringUtils.isNotBlank(localeEntry.getLanguage())) {
        final String name = localeEntry.getCountry() + " (" + localeEntry.getLanguage() + ")";
        init.add(name);
      }
    }
    final ArrayList<String> list = new ArrayList<String>(init);
    Collections.sort(list);
    COUNTRY_LANGUAGE = Collections.unmodifiableList(list);
  }

  private LocaleList() {
    // do not call
  }
}
