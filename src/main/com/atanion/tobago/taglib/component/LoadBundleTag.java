/*
 * Copyright (c) 2003 Atanion GmbH, Germany
 * All rights reserved. Created 16.03.2004 12:53:20.
 * $Id$
 */
package com.atanion.tobago.taglib.component;

import com.atanion.tobago.context.ResourceManagerUtil;
import com.atanion.tobago.taglib.decl.HasVar;
import com.atanion.util.annotation.Tag;
import com.atanion.util.annotation.TagAttribute;
import com.atanion.util.annotation.UIComponentTagAttribute;

import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Load a resource bundle localized for the Locale of the current view
 * from the tobago resource path, and expose it (as a Map) in the request
 * attributes of the current request.
 */
@Tag(name="loadBundle", bodyContent="empty")
public class LoadBundleTag extends TagSupport implements HasVar {
// ----------------------------------------------------------------- attributes

  private String basename;

  private String var;

// ----------------------------------------------------------- business methods

  public int doStartTag() throws JspException {
    Map toStore = new BundleMapWrapper(basename);
    FacesContext.getCurrentInstance().getExternalContext()
        .getRequestMap().put(var, toStore);

    return EVAL_BODY_INCLUDE;
  }

  public void release() {
    basename = null;
    var = null;
  }

// ------------------------------------------------------------ getter + setter

  public String getBasename() {
    return basename;
  }

  /**
   *  Base name of the resource bundle to be loaded.
   */
  @TagAttribute(required=true)
  @UIComponentTagAttribute(type=String.class)
  public void setBasename(String basename) {
    this.basename = basename;
  }

  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }
// -------------------------------------------------------------- inner classes

  private static class BundleMapWrapper implements Map {
    private String basename;

    public BundleMapWrapper(String basename) {
      this.basename = basename;
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
      if (null == key) {
        return false;
      }
      String value = ResourceManagerUtil.getProperty(
          FacesContext.getCurrentInstance(), basename, key.toString());
      return value != null;
    }

    public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
    }

    public Set entrySet() {
      throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
      throw new UnsupportedOperationException();
    }

    public Object get(Object key) {
      if (null == key) {
        return null;
      }
      String value = ResourceManagerUtil.getProperty(
          FacesContext.getCurrentInstance(), basename, key.toString());
      return value;
    }

    public int hashCode() {
      return basename.hashCode();
    }

    public boolean isEmpty() {
      throw new UnsupportedOperationException();
    }

    public Set keySet() {
      throw new UnsupportedOperationException();
    }

    public Object put(Object k, Object v) {
      throw new UnsupportedOperationException();
    }

    public void putAll(Map t) {
      throw new UnsupportedOperationException();
    }

    public Object remove(Object k) {
      throw new UnsupportedOperationException();
    }

    public int size() {
      throw new UnsupportedOperationException();
    }

    public Collection values() {
      throw new UnsupportedOperationException();
    }
  }
}

