/*
 * Copyright (c) 2003 Atanion GmbH, Germany
 * All rights reserved. Created 27.01.2003 14:53:29.
 * $Id$
 */
package com.atanion.tobago.webapp;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.net.URL;

import com.atanion.util.io.IoUtil;

public class TobagoServletMapping {

// ///////////////////////////////////////////// constant

  private static final Log LOG = LogFactory.getLog(TobagoServletMapping.class);


  public static final String TOBAGO_SERVLET_MAPPING
      = TobagoServletMapping.class.getName();

// ///////////////////////////////////////////// attribute

  private String servletName;
  private String urlPrefix;

// ///////////////////////////////////////////// constructor

  public TobagoServletMapping(ServletContext context) {
    init(context);
  }

// ///////////////////////////////////////////// code

  protected void init(ServletContext context) {

    // Prepare a Digester to scan the web application deployment descriptor
    Digester digester = new Digester();
    digester.push(this);
    digester.setNamespaceAware(true);
    digester.setValidating(false);

    final String WEB_APP_DTD = "/javax/servlet/resources/web-app_2_3.dtd";
    URL url;
    try {
      url = ServletContext.class.getResource(WEB_APP_DTD);
      if (null != url) {
        digester.register("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
            url.toString());
      } else {
        if (LOG.isWarnEnabled()) {
          LOG.warn("unable to retrieve local DTD '" + WEB_APP_DTD + "'; trying external URL");
        }
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("unable to retrieve DTD '" + WEB_APP_DTD + "'", e);
      }
      // alternativly use approach below: retrieve dtd from classes
    }

    // todo:
    // Register our local copy of the DTDs that we can find
//      for (int i = 0; i < registrations.length; i += 2) {
//          URL url = this.getClass().getResource(registrations[i+1]);
//          if (url != null)
//              digester.register(registrations[i], url.toString());
//      }

    // Configure the processing rules that we need

    digester.addCallMethod("web-app/servlet",
        "addServlet", 2);
    digester.addCallParam("web-app/servlet/servlet-name", 0);
    digester.addCallParam("web-app/servlet/servlet-class", 1);

    digester.addCallMethod("web-app/servlet-mapping",
        "addServletMapping", 2);
    digester.addCallParam("web-app/servlet-mapping/servlet-name", 0);
    digester.addCallParam("web-app/servlet-mapping/url-pattern", 1);

    // Process the web application deployment descriptor
//    if (Log.isDebugEnabled()) {
//      Log.debug("Scanning web.xml for controller servlet mapping");
//    }
    InputStream input = null;
    try {
      input = context.getResourceAsStream("/WEB-INF/web.xml");
      digester.parse(input);
    } catch (Throwable e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("configWebXml", e);
      }
    } finally {
      IoUtil.close(input);
    }

    // Record a servlet context attribute (if appropriate)
//    if (Log.isDebugEnabled()) {
//      Log.debug("Mapping for servlet '" + servletName + "' = '" +
//          servletMapping + "'");
//    }

  }

  public void addServlet(String servletName, String servletClass) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("addServlet(" + servletName + "," + servletClass + ")");
    }
    if (FacesServlet.class.getName().equals(servletClass)) {
      if (this.servletName != null) {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Overwriting servlet name from '" + this.servletName
              + "' with '" + servletName + "'");
        }
      }
      this.servletName = servletName;
    }
  }

  public void addServletMapping(String servletName, String urlPattern) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("addServletMapping(" + servletName + "," + urlPattern + ")");
    }
    if (this.servletName.equals(servletName)) {

      if (!urlPattern.endsWith("/*")) {
        throw new FacesException("Unsupported urlPattern in web.xml " +
            "(must end with '/*'): " + urlPattern);
      }

      if (urlPrefix != null) {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Ambiguous servletmapping! Can't find unique pattern for "
              + "servlet-name='" + servletName + "' in web.xml!");
          LOG.warn("Possible values are '" + urlPattern + "' and '"
              + urlPrefix + "/*" + "'");
        }
      }
      urlPrefix = urlPattern.substring(0, urlPattern.length() - 2);
    }
  }

// ///////////////////////////////////////////// bean getter + setter

  public String getUrlPrefix() {
    return urlPrefix;
  }

}
