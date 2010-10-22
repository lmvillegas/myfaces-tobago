package org.apache.myfaces.tobago.example.demo.overview;

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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class ValidationSeverity {

  public void addFatal(FacesContext facesContext, UIComponent component, Object value) {
    FacesMessage message = new FacesMessage(
        FacesMessage.SEVERITY_FATAL, "Custom fatal", "This is a custom fatal error");
    facesContext.addMessage(component.getClientId(facesContext), message);
  }

  public void addError(FacesContext facesContext, UIComponent component, Object value) {
    FacesMessage message = new FacesMessage(
        FacesMessage.SEVERITY_ERROR, "Custom error", "This is a custom error");
    facesContext.addMessage(component.getClientId(facesContext), message);
  }

  public void addWarn(FacesContext facesContext, UIComponent component, Object value) {
    FacesMessage message = new FacesMessage(
        FacesMessage.SEVERITY_WARN, "Custom warning", "This is a custom warning");
    facesContext.addMessage(component.getClientId(facesContext), message);
  }

  public void addInfo(FacesContext facesContext, UIComponent component, Object value) {
    FacesMessage message = new FacesMessage(
        FacesMessage.SEVERITY_INFO, "Custom info", "This is a custom information");
    facesContext.addMessage(component.getClientId(facesContext), message);
  }
}
