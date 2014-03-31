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

import org.apache.myfaces.tobago.component.UIPopup;
import org.apache.myfaces.tobago.context.Markup;
import org.apache.myfaces.tobago.internal.component.AbstractUIPage;
import org.apache.myfaces.tobago.internal.layout.LayoutContext;
import org.apache.myfaces.tobago.internal.util.FacesContextUtils;
import org.apache.myfaces.tobago.layout.Measure;
import org.apache.myfaces.tobago.renderkit.LayoutComponentRendererBase;
import org.apache.myfaces.tobago.renderkit.css.Classes;
import org.apache.myfaces.tobago.renderkit.css.Style;
import org.apache.myfaces.tobago.renderkit.html.HtmlElements;
import org.apache.myfaces.tobago.renderkit.html.util.HtmlRendererUtils;
import org.apache.myfaces.tobago.util.ComponentUtils;
import org.apache.myfaces.tobago.webapp.TobagoResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class PopupRenderer extends LayoutComponentRendererBase {

  private static final Logger LOG = LoggerFactory.getLogger(PopupRenderer.class);

  @Override
  public boolean getRendersChildren() {
    return true;
  }

  @Override
  public void prepareRender(FacesContext facesContext, UIComponent component) throws IOException {

    UIPopup popup = (UIPopup) component;

    FacesContextUtils.addPopup(facesContext, popup);

    FacesContextUtils.addScriptBlock(facesContext, "jQuery(document).ready(function() {Tobago.Popup.setup();});");

    super.prepareRender(facesContext, popup);

    if (popup.isModal()) {
      ComponentUtils.addCurrentMarkup(popup, Markup.MODAL);
    }
  }

  @Override
  public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
    TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);
    // TODO check ajaxId
    if (FacesContextUtils.isAjax(facesContext)) {
      writer.startJavascript();
      writer.write("Tobago.Popup.setup();");
      writer.endJavascript();
    }
    
    UIPopup popup = (UIPopup) component;

    LayoutContext layoutContext = new LayoutContext(popup);
    layoutContext.layout();

    // XXX fixing invisible popups
    if (popup.getCurrentWidth() == null || popup.getCurrentWidth().equals(Measure.ZERO)) {
      LOG.warn("Undefined width of popup with id='" + popup.getClientId(facesContext) + "'");
      popup.setCurrentWidth(getPreferredWidth(facesContext, popup));
    }
    if (popup.getCurrentHeight() == null || popup.getCurrentHeight().equals(Measure.ZERO)) {
      LOG.warn("Undefined height of popup with id='" + popup.getClientId(facesContext) + "'");
      popup.setCurrentHeight(getPreferredHeight(facesContext, popup));
    }

    final String clientId = popup.getClientId(facesContext);

    // XXX May be computed in the "Layout Manager Phase"
    AbstractUIPage page = ComponentUtils.findPage(facesContext);
    if (popup.getLeft() == null) {
      popup.setLeft(page.getCurrentWidth().subtract(popup.getCurrentWidth()).divide(2));
    }
    if (popup.getTop() == null) {
      popup.setTop(page.getCurrentHeight().subtract(popup.getCurrentHeight()).divide(2));
    }

    writer.startElement(HtmlElements.DIV, popup);
    writer.writeIdAttribute(clientId);
    HtmlRendererUtils.writeDataAttributes(facesContext, writer, popup);
    Style style = new Style(facesContext, popup);
    Integer zIndex = popup.getZIndex();
    if (zIndex == null) {
      zIndex = 100;
      LOG.warn("No z-index found for UIPopup. Set to " + zIndex);
    }
    style.setZIndex(zIndex);
    writer.writeStyleAttribute(style);
    writer.writeClassAttribute(Classes.create(popup));
  }

  @Override
  public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
    TobagoResponseWriter writer = HtmlRendererUtils.getTobagoResponseWriter(facesContext);
    writer.endElement(HtmlElements.DIV);
  }
}