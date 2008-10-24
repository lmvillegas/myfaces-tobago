package org.apache.myfaces.tobago.component;

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

import static org.apache.myfaces.tobago.TobagoConstants.ATTR_SELECTABLE;
import org.apache.myfaces.tobago.model.MixedTreeModel;
import org.apache.myfaces.tobago.util.ComponentUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractUITree extends UIInput implements NamingContainer {

  public static final String COMPONENT_TYPE = "org.apache.myfaces.tobago.Tree";
  public static final String MESSAGE_NOT_LEAF = "tobago.tree.MESSAGE_NOT_LEAF";

  public static final String SEP = "-";

  public static final String TREE_STATE = SEP + "treeState";
  public static final String SELECT_STATE = SEP + "selectState";
  public static final String MARKER = SEP + "marker";

  public static final String PARAMETER_TREE_NODE_ID = "treeNodeId";

 /* private Boolean showJunctions = true;
  private Boolean showIcons = true;
  private Boolean showRoot = true;
  private Boolean showRootJunction = true;

  private String mode;*/
  private MixedTreeModel model;


  public UIComponent getRoot() {
    // find the UITreeNode in the childen.
    for (UIComponent child : (List<UIComponent>) getChildren()) {
      if (child instanceof AbstractUITreeNode) {
        return child;
      }
      if (child instanceof AbstractUITreeData) {
        return child;
      }
    }
    return null;
  }

  public void encodeEnd(FacesContext context) throws IOException {
    model = new MixedTreeModel();

    buildModel();
    super.encodeEnd(context);
  }

  private void buildModel() {
    for (Object child : getChildren()) {
      if (child instanceof TreeModelBuilder) {
        TreeModelBuilder builder = (TreeModelBuilder) child;
        builder.buildBegin(model);
        builder.buildChildren(model);
        builder.buildEnd(model);
      }
    }
  }

  public boolean getRendersChildren() {
    return true;
  }

  public boolean isSelectableTree() {
    final Object selectable
        = ComponentUtil.getAttribute(this, ATTR_SELECTABLE);
    return selectable != null
        && (selectable.equals("multi") || selectable.equals("multiLeafOnly")
        || selectable.equals("single") || selectable.equals("singleLeafOnly")
        || selectable.equals("sibling") || selectable.equals("siblingLeafOnly"));
  }

  public void processDecodes(FacesContext facesContext) {

    if (!isRendered()) {
      return;
    }

    if (ComponentUtil.isOutputOnly(this)) {
      setValid(true);
    } else {
      // in tree first decode node and than decode children

      decode(facesContext);

      for (Iterator i = getFacetsAndChildren(); i.hasNext();) {
        UIComponent uiComponent = ((UIComponent) i.next());
        uiComponent.processDecodes(facesContext);
      }
    }
  }

  public void validate(FacesContext context) {

// todo: validate must be written new, without TreeState
/*
    if (isRequired() && getState().getSelection().size() == 0) {
      setValid(false);
      FacesMessage facesMessage = MessageFactory.createFacesMessage(context,
          UISelectOne.MESSAGE_VALUE_REQUIRED, FacesMessage.SEVERITY_ERROR);
      context.addMessage(getClientId(context), facesMessage);
    }

    String selectable = ComponentUtil.getStringAttribute(this, ATTR_SELECTABLE);
    if (selectable != null && selectable.endsWith("LeafOnly")) {

      Set<DefaultMutableTreeNode> selection = getState().getSelection();

      for (DefaultMutableTreeNode node : selection) {
        if (!node.isLeaf()) {
          FacesMessage facesMessage = MessageFactory.createFacesMessage(
              context, MESSAGE_NOT_LEAF, FacesMessage.SEVERITY_ERROR);
          context.addMessage(getClientId(context), facesMessage);
          break; // don't continue iteration, no dublicate messages needed
        }
      }
    }
*/
//  call all validators
    if (getValidators() != null) {
      for (Validator validator : getValidators()) {
        try {
          validator.validate(context, this, null);
        } catch (ValidatorException ve) {
          // If the validator throws an exception, we're
          // invalid, and we need to add a message
          setValid(false);
          FacesMessage message = ve.getFacesMessage();
          if (message != null) {
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(getClientId(context), message);
          }
        }
      }
    }
  }

  public void updateModel(FacesContext facesContext) {
    // nothig to update for tree's
    // TODO: updateing the model here and *NOT* in the decode phase
  }

  public abstract String getMode();

  public abstract boolean isShowIcons();

  public abstract boolean isShowJunctions();

  public abstract boolean isShowRootJunction();
   
  public abstract boolean isShowRoot();

  /*public Object saveState(FacesContext context) {
   Object[] state = new Object[6];
   state[0] = super.saveState(context);
   state[1] = showJunctions;
   state[2] = showIcons;
   state[3] = showRoot;
   state[4] = showRootJunction;
   state[5] = mode;
   return state;
 }

 public void restoreState(FacesContext context, Object state) {
   Object[] values = (Object[]) state;
   super.restoreState(context, values[0]);
   showJunctions = (Boolean) values[1];
   showIcons = (Boolean) values[2];
   showRoot = (Boolean) values[3];
   showRootJunction = (Boolean) values[4];
   mode = (String) values[5];
 }

 public boolean isShowJunctions() {
   if (showJunctions != null) {
     return (showJunctions);
   }
   ValueBinding vb = getValueBinding(ATTR_SHOW_JUNCTIONS);
   if (vb != null) {
     return (!Boolean.FALSE.equals(vb.getValue(getFacesContext())));
   } else {
     return (this.showJunctions);
   }
 }

 public void setShowJunctions(boolean showJunctions) {
   this.showJunctions = showJunctions;
 }

 public boolean isShowIcons() {
   if (showIcons != null) {
     return (showIcons);
   }
   ValueBinding vb = getValueBinding(ATTR_SHOW_ICONS);
   if (vb != null) {
     return (!Boolean.FALSE.equals(vb.getValue(getFacesContext())));
   } else {
     return (this.showIcons);
   }
 }

 public void setShowIcons(boolean showIcons) {
   this.showIcons = showIcons;
 }

 public boolean isShowRoot() {
   if (showRoot != null) {
     return (showRoot);
   }
   ValueBinding vb = getValueBinding(ATTR_SHOW_ROOT);
   if (vb != null) {
     return (!Boolean.FALSE.equals(vb.getValue(getFacesContext())));
   } else {
     return (this.showRoot);
   }
 }

 public void setShowRoot(boolean showRoot) {
   this.showRoot = showRoot;

 }

 public boolean isShowRootJunction() {
   if (showRootJunction != null) {
     return (showRootJunction);
   }
   ValueBinding vb = getValueBinding(ATTR_SHOW_ROOT_JUNCTION);
   if (vb != null) {
     return (!Boolean.FALSE.equals(vb.getValue(getFacesContext())));
   } else {
     return (this.showRootJunction);
   }
 }

 public void setShowRootJunction(boolean showRootJunction) {
   this.showRootJunction = showRootJunction;
 } */

  public static class Command implements Serializable {
    private String command;

    public Command(String command) {
      this.command = command;
    }

    public String getCommand() {
      return command;
    }
  }

  public MixedTreeModel getModel() {
    return model;
  }
}
