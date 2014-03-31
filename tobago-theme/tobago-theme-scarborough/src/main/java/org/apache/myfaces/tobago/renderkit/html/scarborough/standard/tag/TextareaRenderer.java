/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.tobago.renderkit.html.scarborough.standard.tag;

import org.apache.myfaces.tobago.component.Attributes;
import org.apache.myfaces.tobago.component.UITextarea;
import org.apache.myfaces.tobago.renderkit.HtmlUtils;
import org.apache.myfaces.tobago.renderkit.InputRendererBase;
import org.apache.myfaces.tobago.renderkit.css.Classes;
import org.apache.myfaces.tobago.renderkit.css.Style;
import org.apache.myfaces.tobago.renderkit.html.HtmlAttributes;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtils;
import org.apache.myfaces.tobago.renderkit.util.RenderUtils;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.LengthValidator;
import javax.faces.validator.Validator;
import java.io.IOException;

public class TextareaRenderer extends InputRendererBase {

  private static final Logger LOG = LoggerFactory.getLogger(TextareaRenderer.class);

  @Override
  public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
    if (!(component instanceof UITextarea)) {
      LOG.error("Wrong type: Need " + UITextarea.class.getName() + ", but was " + component.getClass().getName());
      return;
    }

    UITextarea input = (UITextarea) component;
    String title = HtmlRendererUtils.getTitleFromTipAndMessages(facesContext, component);

    String clientId = input.getClientId(facesContext);
    String onchange = HtmlUtils.generateOnchange(input, facesContext);

    TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);
    writer.startElement(HtmlElements.TEXTAREA, input);
    writer.writeNameAttribute(clientId);
    writer.writeIdAttribute(clientId);
    HtmlRendererUtils.writeDataAttributes(facesContext, writer, input);
    writer.writeAttribute(HtmlAttributes.ROWS, null, Attributes.ROWS);
    if (title != null) {
      writer.writeAttribute(HtmlAttributes.TITLE, title, true);
    }
    writer.writeAttribute(HtmlAttributes.READONLY, input.isReadonly());
    writer.writeAttribute(HtmlAttributes.DISABLED, input.isDisabled());
    Integer tabIndex = input.getTabIndex();
    if (tabIndex != null) {
      writer.writeAttribute(HtmlAttributes.TABINDEX, tabIndex);
    }
    HtmlRendererUtils.renderDojoDndItem(component, writer, true);

    writer.writeClassAttribute(Classes.create(input));
    Style style = new Style(facesContext, input);
    writer.writeStyleAttribute(style);
    if (onchange != null) {
      writer.writeAttribute(HtmlAttributes.ONCHANGE, onchange, null);
    }
    /*int maxLength = -1;
    for (Validator validator : input.getValidators()) {
      if (validator instanceof LengthValidator) {
        LengthValidator lengthValidator = (LengthValidator) validator;
        maxLength = lengthValidator.getMaximum();
      }
    }
    if (maxLength > 0) {
      writer.writeAttribute(HtmlAttributes.MAXLENGTH, maxLength);
    }
    String placeholder = input.getPlaceholder();
    if (placeholder != null) {
      writer.writeAttribute(HtmlAttributes.PLACEHOLDER, placeholder, true);
    }*/
    String currentValue = RenderUtils.currentValue(input);
    if (currentValue != null) {
      // this is because browsers eat the first CR+LF of <textarea>
      if (currentValue.startsWith("\r\n")) {
        currentValue = "\r\n" + currentValue;
      } else if (currentValue.startsWith("\n")) {
        currentValue = "\n" + currentValue;
      } else if (currentValue.startsWith("\r")) {
        currentValue = "\r" + currentValue;
      }
      writer.writeText(currentValue);
    }
    writer.endElement(HtmlElements.TEXTAREA);
    /*if (placeholder != null && !VariableResolverUtils.resolveClientProperties(facesContext)
        .getUserAgent().hasCapability(Capability.PLACEHOLDER)) {
      HtmlRendererUtils.createPlaceholderDiv(input, currentValue, placeholder, style, writer);
    }*/

    HtmlRendererUtils.checkForCommandFacet(input, facesContext, writer);
    int maxLength = -1;
    for (Validator validator : input.getValidators()) {
      if (validator instanceof LengthValidator) {
        LengthValidator lengthValidator = (LengthValidator) validator;
        maxLength = lengthValidator.getMaximum();
      }
    }
    boolean required = ComponentUtils.getBooleanAttribute(input, Attributes.REQUIRED);
    if (required || maxLength > 0) {
      final String[] cmds = {
          "new Tobago.In(\"" + input.getClientId(facesContext) + "\"," + required + ",\""
              + Classes.requiredWorkaround(input) + "\" " + (maxLength > -1 ? "," + maxLength : "") + "  );"
      };
      HtmlRendererUtils.writeScriptLoader(facesContext, null, cmds);
    }

    // focus
    HtmlRendererUtils.renderFocusId(facesContext, input);
  }
}