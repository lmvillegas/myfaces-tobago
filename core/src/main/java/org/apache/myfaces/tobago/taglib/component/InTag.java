package org.apache.myfaces.tobago.taglib.component;

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

/*
 * Created on: 15.02.2002, 17:01:56
 * $Id$
 */

import static org.apache.myfaces.tobago.TobagoConstants.ATTR_PASSWORD;
import org.apache.myfaces.tobago.component.ComponentUtil;
import org.apache.myfaces.tobago.component.UIInput;
import org.apache.myfaces.tobago.util.Deprecation;

import javax.faces.component.UIComponent;

public class InTag extends TextInputTag implements InTagDeclaration {

  private String password;
  private String suggestMethod;
  private String markup;


  @Override
  public void release() {
    super.release();
    password = null;
    suggestMethod = null;
    markup = null;
  }

  @Override
  protected void setProperties(UIComponent component) {
    super.setProperties(component);

    if (getLabel() != null && Deprecation.LOG.isErrorEnabled()) {
      Deprecation.LOG.error("the label attribute is deprecated in tc:in, please use tx:in instead.");
    }

    ComponentUtil.setBooleanProperty(component, ATTR_PASSWORD, password);
    if (component instanceof UIInput) {
      ComponentUtil.setSuggestMethodBinding((UIInput) component, suggestMethod);
    }
    ComponentUtil.setMarkup(component, markup);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSuggestMethod() {
    return suggestMethod;
  }

  public void setSuggestMethod(String suggestMethod) {
    this.suggestMethod = suggestMethod;
  }


  public String getMarkup() {
    return markup;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }
}
