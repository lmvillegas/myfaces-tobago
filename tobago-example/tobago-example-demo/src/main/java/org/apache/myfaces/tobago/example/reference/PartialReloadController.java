package org.apache.myfaces.tobago.example.reference;

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

import org.apache.myfaces.tobago.ajax.AjaxUtils;
import org.apache.myfaces.tobago.example.demo.Navigation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import java.util.Date;

public class PartialReloadController {

  private static final Logger LOG = LoggerFactory.getLogger(PartialReloadController.class);

  private String navigateAction;

  public Date getCurrentDate() {
    return new Date();
  }

  public String reload() {
    return logAndNavigate(null);
  }

  public String waitAndReload() {
    synchronized (this) {
      try {
        wait(6000);
      } catch (InterruptedException e) {
        //
      }
    }
    return logAndNavigate(null);
  }

  public String navigateAction() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    VariableResolver resolver = facesContext.getApplication().getVariableResolver();
    Navigation navigation = (Navigation) resolver.resolveVariable(facesContext, "navigation");

    // in case of both the select control is not processed during lifecycle
    // we need to get the value from the request params
    navigateAction = (String) facesContext.getExternalContext().getRequestParameterMap().get("page:content:navSelect");

    LOG.info("navigateAction = \"" + navigateAction + "\"");
    if (navigateAction == null) {
      return logAndNavigate(null);
    } else if ("parent".equals(navigateAction)) {
      navigateAction = null;
      AjaxUtils.addAjaxComponent(facesContext, "page:content:parent");
      return logAndNavigate(null);
    } else if ("both".equals(navigateAction)) {
      AjaxUtils.addAjaxComponent(facesContext, "page:content:left");
      AjaxUtils.addAjaxComponent(facesContext, "page:content:right");
      navigateAction = null;
      return logAndNavigate(null);
    } else if ("prev".equals(navigateAction)) {
      navigateAction = null;
      return logAndNavigate(navigation.gotoPrevious());
    } else if ("next".equals(navigateAction)) {
      navigateAction = null;
      return logAndNavigate(navigation.gotoNext());
    }
    return logAndNavigate(null);
  }

  private String logAndNavigate(String navValue) {
    LOG.info("Return navigate value: " + navValue + "");
    return navValue;
  }


  public String getNavigateAction() {
    return navigateAction;
  }

  public void setNavigateAction(String navigateAction) {
    this.navigateAction = navigateAction;
  }
}
