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

package org.apache.myfaces.tobago.webapp;

import org.apache.myfaces.tobago.config.TobagoConfig;
import org.apache.myfaces.tobago.internal.config.ContentSecurityPolicy;
import org.apache.myfaces.tobago.internal.config.TobagoConfigBuilder;
import org.apache.myfaces.tobago.internal.util.MimeTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TobagoServletContextListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(TobagoServletContextListener.class);

  @Override
  public void contextInitialized(final ServletContextEvent event) {

    if (LOG.isInfoEnabled()) {
      LOG.info("*** contextInitialized ***");
    }

    final ServletContext servletContext = event.getServletContext();

    if (servletContext.getAttribute(TobagoConfig.TOBAGO_CONFIG) != null) {
      LOG.warn("Tobago has been already initialized. Do nothing.");
    } else {
      TobagoConfigBuilder.init(servletContext);
    }

    if (LOG.isInfoEnabled()) {
      final TobagoConfig tobagoConfig = TobagoConfig.getInstance(servletContext);
      LOG.info("TobagoConfig: " + tobagoConfig);

      MimeTypeUtils.init(servletContext);

      final ContentSecurityPolicy.Mode mode = tobagoConfig.getContentSecurityPolicy().getMode();
      final StringBuilder builder = new StringBuilder();
      builder.append("\n*************************************************************************************");
      builder.append("\nNote: CSP is ");
      builder.append(mode);
      if (mode == ContentSecurityPolicy.Mode.ON) {
        builder.append("\nYou may need to check application specific JavaScript code.");
        builder.append("\nOtherwise the application will not run in modern browsers, that are supporting CSP.");
        builder.append("\nFor more information see http://myfaces.apache.org/tobago/migration-2.0.html");
      }
      builder.append("\n*************************************************************************************");
      final String note = builder.toString();
      LOG.info(note);
    }
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    if (LOG.isInfoEnabled()) {
      LOG.info("*** contextDestroyed ***\n--- snip ---------"
          + "--------------------------------------------------------------");
    }

    final ServletContext servletContext = event.getServletContext();

    servletContext.removeAttribute(TobagoConfig.TOBAGO_CONFIG);
  }

}
