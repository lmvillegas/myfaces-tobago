/*
 *    Copyright 2002-2005 The Apache Software Foundation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

Tobago.Sheets = {
  sheets: {},

  get: function(id) {
    return this.sheets[id];
  },

  put: function(sheet) {
    this.sheets[sheet.sheetId] = sheet;
  },

  selectAll: function(id) {
    var sheet = this.get(id).selectAll();
  },

  unSelectAll: function(id) {
    var sheet = this.get(id).unSelectAll();
  },

  toggleAllSelections: function(id) {
    var sheet = this.get(id).toggleAllSelections();
  }

};

Tobago.Sheet = function(sheetId, enableAjax, checkedImage, uncheckedImage) {
  this.id = sheetId;
  this.ajaxEnabled = enableAjax;
  this.checkedImage = checkedImage;
  this.uncheckedImage = uncheckedImage;

  this.resizerId = undefined;
  this.oldX = 0;
  this.newWidth = 0;

  this.outerDiv = Tobago.element(this.id + "_outer_div");

  if (this.ajaxEnabled) {
    this.prototype = new Ajax.Base();
  }

  this.sortOnclickRegExp
      = new RegExp("Tobago.submitAction\\(('|\")(.*?)('|\")\\)");

  this.ppPrefix
      = Tobago.SUB_COMPONENT_SEP + "pagingPages" + Tobago.SUB_COMPONENT_SEP;

  this.firstRegExp = new RegExp(this.ppPrefix + "First$");

  this.prevRegExp = new RegExp(this.ppPrefix + "Prev$");

  this.nextRegExp = new RegExp(this.ppPrefix + "Next$");

  this.lastRegExp = new RegExp(this.ppPrefix + "Last$");

  this.firstRowRegExp = new RegExp("^" + this.id + "_data_tr_\\d+$");

  this.setupElements = function() {
    this.headerDiv = Tobago.element(this.id + "_header_div");
    this.contentDiv = Tobago.element(this.id + "_data_div");
    this.contentTable = this.contentDiv.getElementsByTagName("table")[0];

    this.selected
        = Tobago.element(this.id + Tobago.SUB_COMPONENT_SEP +"selected");
    this.headerWidths = Tobago.element(this.id + "::widths");

    this.firstRow = this.getFirstRow();
    this.firstRowIndex = (this.firstRow != undefined)
        ? this.firstRow.id.substring(this.firstRow.id.lastIndexOf("_data_tr_") + 9)
        : -1;

  };

  this.setupSortHeaders = function() {
    var i = 0;
    var idPrefix = this.id + "_header_box_";
    var headerBox = Tobago.element(idPrefix + i++);
    while (headerBox) {
      if (headerBox.onclick) {
        var match = this.sortOnclickRegExp.exec(headerBox.onclick.valueOf());
//        LOG.debug("match[0] = " + match[0]);
//        LOG.debug("match[1] = " + match[1]);
//        LOG.debug("*match[2] = " + match[2]);
//        LOG.debug("match[3] = " + match[3]);
//        LOG.debug("match[4] = " + match[4]);
//        LOG.debug("match[5] = " + match[5]);
//        LOG.debug("match[6] = " + match[6]);
//        headerBox.formId = match[2];
        headerBox.sorterId = match[2];
//        delete headerBox.onclick;
        headerBox.onclick = null;
//        LOG.debug("headerBox.id = " + headerBox.id);
        Tobago.addBindEventListener(headerBox, "click", this, "doSort");
      }
      headerBox = Tobago.element(idPrefix + i++);
    }
  };

  this.setupPagingLinks = function() {
    idPrefix = this.id + Tobago.SUB_COMPONENT_SEP;
    var linkBox = Tobago.element(idPrefix + "pagingLinks");
    if (linkBox) {
      for (i = 0 ; i < linkBox.childNodes.length ; i++) {
        var child = linkBox.childNodes[i];
        if (child.nodeType == 1 && child.tagName.toUpperCase() == "A") {
          child.href = "#";
          Tobago.addBindEventListener(child, "click", this, "doPagingDirect");
        }
      }
    }
  };

  this.setupPagePaging = function() {
    linkBox = Tobago.element(idPrefix + "pagingPages");
    if (linkBox) {
      for (i = 0 ; i < linkBox.childNodes.length ; i++) {
        var child = linkBox.childNodes[i];
        if (child.nodeType == 1 && child.tagName.toUpperCase() == "IMG") {
          // first, prev, next and last commands
          if (child.onclick) {
//            delete child.onclick;
            child.onclick = null;
            Tobago.addBindEventListener(child, "click", this, "doPaging");
          }
        } else if (child.nodeType == 1 && child.tagName.toUpperCase() == "SPAN") {
//          LOG.debug("Page : onclick =" + child.onclick);
          if (child.onclick) {
//            delete child.onclick;
            child.onclick = null;
            var toPageId = this.id + Tobago.COMPONENT_SEP + "ToPage";
            Tobago.addEventListener(child, "click",
                Tobago.bind(this, "insertTarget", toPageId));
          }

        }
      }
    }
  };

  this.setupRowPaging = function() {
    var toRowId = this.id + Tobago.COMPONENT_SEP + "ToRow";
    var rowText = Tobago.element(toRowId + Tobago.SUB_COMPONENT_SEP + "text");
    if (rowText) {
      var parent = rowText.parentNode;
//      LOG.debug("row : onclick =" + parent.onclick);
      if (parent.onclick) {
//        delete parent.onclick;
        parent.onclick = null;
        Tobago.addEventListener(parent, "click",
            Tobago.bind(this, "insertTarget", toRowId));
      }
    }
  };

  this.doSort = function(event) {
    var element = Event.element(event);
    while (element && !element.sorterId) {
      element = element.parentNode;
    }
//    LOG.debug("element.id = " + element.id);
//    LOG.debug("sorterId = " + element.sorterId);
    this.reloadWithAction(element.sorterId);
  };

  this.doPagingDirect = function(event) {
    var element = Event.element(event);
    var action = this.id + Tobago.COMPONENT_SEP + "ToPage";

    var page = element.id.lastIndexOf('_');
    page = element.id.substring(page + 1);
    var hidden = document.createElement('input');
    hidden.type = 'hidden';
    hidden.value = page;
    hidden.name = action + Tobago.SUB_COMPONENT_SEP +  "value";
    this.outerDiv.appendChild(hidden);

    this.reloadWithAction(action);
  };

  this.doPaging = function(event) {
    var element = Event.element(event);
    var action = "unset";
    if (element.id.match(this.firstRegExp)){
      action = this.id + Tobago.COMPONENT_SEP +"First";
    } else if (element.id.match(this.prevRegExp)){
      action = this.id + Tobago.COMPONENT_SEP +"Prev";
    } else if (element.id.match(this.nextRegExp)){
      action = this.id + Tobago.COMPONENT_SEP +"Next";
    } else if (element.id.match(this.lastRegExp)){
      action = this.id + Tobago.COMPONENT_SEP +"Last";
    }
    this.reloadWithAction(action);
  };

  this.reloadWithAction = function(action) {
    LOG.debug("reload sheet with action \"" + action + "\"");
    Tobago.Updater.update(this.outerDiv, null, action, this.id, this.options);

  };

  this.insertTarget = function(event, actionId) {
//    LOG.debug("insertTarget('" + actionId + "')")
    var textId = actionId + Tobago.SUB_COMPONENT_SEP + "text";
    var text = Tobago.element(textId);
    if (text) {
      var span = text.parentNode;
      var hiddenId = actionId + Tobago.SUB_COMPONENT_SEP +  "value";
      span.style.cursor = 'auto';
      var input = Tobago.element(hiddenId);
      if (! input) {
        input = document.createElement('input');
        input.type='text';
        input.id=hiddenId;
        input.name=hiddenId;
        input.className = "tobago-sheet-paging-input";
        input.actionId = actionId;
        Tobago.addBindEventListener(input, "blur", this, "delayedHideInput");
        Tobago.addBindEventListener(input, "keydown", this, "doKeyEvent");
        input.style.display = 'none';
        span.insertBefore(input, text);
      }
      input.value=text.innerHTML;
      input.style.display = '';
      text.style.display = 'none';
      input.focus();
      input.select();
    }
    else {
      LOG.error("Can't find text field with id = \"" + textId + "\"!");
    }
  };

  this.delayedHideInput = function(event) {
    var element = Event.element(event);
    if (element) {
      this.textInput = element;
      setTimeout(Tobago.bind(this, "hideInput"), 100);
    }
  };

  this.hideInput = function() {
    if (this.textInput) {
      this.textInput.parentNode.style.cursor = 'pointer';
      this.textInput.style.display = 'none';
      this.textInput.nextSibling.style.display = '';
    }
  };

  this.doKeyEvent = function(event) {
    var input = Event.element(event);
    if (input) {

      var keyCode;
      if (event.which) {                  
        keyCode = event.which;
      } else {
        keyCode = event.keyCode;
      }
      if (keyCode == 13) {
        if (input.value != input.nextSibling.innerHTML) {
          this.reloadWithAction(input.actionId);
        }
        else {
          this.textInput = input;
          this.hideInput();
        }
      }
    }
  };

  this.onComplete = function() {
    LOG.debug("sheet reloaded");
    this.setup();
  };

  this.options = {
    method: 'post',
    asynchronous: true,
    onComplete: Tobago.bind(this, "onComplete"),
    parameters: '',
    evalScripts: true
  };

  this.setupResizer = function() {
    var i = 0;
    Tobago.addBindEventListener(this.headerDiv, "mousemove", this, "doResize");
    Tobago.addBindEventListener(this.headerDiv, "mouseup", this, "endResize");
    Tobago.addBindEventListener(this.contentDiv, "scroll", this, "doScroll");
    var resizer = Tobago.element(this.id + "_header_resizer_" + i++ );
    while (resizer) {
      if (resizer.className.match(/tobago-sheet-header-resize-cursor/)) {
        Tobago.addEventListener(resizer, "click", Tobago.stopEventPropagation);
        Tobago.addBindEventListener(resizer, "mousedown", this, "beginResize");
      }
      resizer = Tobago.element(this.id + "_header_resizer_" + i++ );
    }
  };

  this.setup = function() {
    LOG.debug("setup(" + this.id +")");

    // ToDo: find a better way to fix this problem
    // IE needs this in case of ajax loading of style classes
    this.outerDiv.className = this.outerDiv.className;
    this.outerDiv.innerHTML = this.outerDiv.innerHTML;

    this.setupElements();

    this.setupResizer();

    this.adjustHeaderDiv();
    this.adjustResizer();

    this.setupHeader();

    if (this.firstRow) {
      this.tobagoLastClickedRowId = this.firstRowIndex;
    }
    this.addSelectionListener();
    this.adjustScrollBars();
    this.updateSelectionView();

    if (this.ajaxEnabled) {
      this.setupSortHeaders();
      this.setupPagingLinks();
      this.setupPagePaging();
      this.setupRowPaging();
    }
  };

  this.adjustScrollBars = function() {
    var dataFiller = Tobago.element(this.id + "_data_row_0_column_filler");
    if (dataFiller) {
      var tableWidth = this.contentTable.style.width;
      this.contentTable.style.width = "10px";
      dataFiller.style.width = "0px";
      var clientWidth = this.contentDiv.clientWidth;

      if (this.contentDiv.scrollWidth <= clientWidth) {
        var width = 0;
        var i = 0;
        var cellDiv = Tobago.element(this.id + "_data_row_0_column" + i++);
        while (cellDiv) {
          var tmp = cellDiv.style.width.replace(/px/, "") - 0 ;
          width += tmp;
          cellDiv = Tobago.element(this.id + "_data_row_0_column" + i++);
        }
        dataFiller.style.width = Math.max((clientWidth - width), 0) + "px";
      }
      this.contentTable.style.width = tableWidth;
    }
  };

  this.addSelectionListener = function() {
    var row = this.firstRow;
    if (row) {
      var i = this.firstRowIndex;
      i++;
      while (row) {
        //       LOG.debug("rowId = " + row.id + "   next i=" + i);
        Tobago.addBindEventListener(row, "click", this, "doSelection");
        row = Tobago.element(this.id + "_data_tr_" + i++ );
      }
      //LOG.debug("preSelected rows = " + Tobago.element(sheetId + "::selected").value);
    }
  };

  this.doSelection = function(event) {
    if (! event) {
      event = window.event;
    }

    Tobago.clearSelection();

    //LOG.debug("event.ctrlKey = " + event.ctrlKey);
    //LOG.debug("event.shiftKey = " + event.shiftKey);

    var srcElement = Tobago.element(event);
    //  LOG.debug("srcElement = " + srcElement.tagName);
    if (! Tobago.isInputElement(srcElement.tagName)) {

      var dataRow = Tobago.element(event);
      while (dataRow.id.search(new RegExp("_data_tr_\\d+$")) == -1) {
        dataRow = dataRow.parentNode;
      }
      var rowId = dataRow.id;
      //LOG.debug("rowId = " + rowId);
      var rowIndex = rowId.substring(rowId.lastIndexOf("_data_tr_") + 9);
      var selector = Tobago.element(this.id + "_data_row_selector_" + rowIndex);

      if (! event.ctrlKey && ! selector) {
        // clearAllSelections();
        this.selected.value = "";
      }

      if (event.shiftKey) {
        this.selectRange(dataRow);
      }
      else {
        this.tobagoLastClickedRowId = rowId;
        this.toggleSelectionForRow(dataRow);
      }
      this.updateSelectionView();
      //LOG.debug("selected rows = " + hidden.value);
    }
  };

  this.updateSelectionView = function(sheetId) {
    var selected = this.selected.value;
    var row = this.firstRow;
    var i = this.firstRowIndex;
    while (row) {

      var selector = Tobago.element(this.id + "_data_row_selector_" + i);
      var re = new RegExp("," + i +",");
      var classes = row.className;
      var img = Tobago.element(this.id + "_data_row_selector_" + i);
      if (selected.search(re) == -1) { // not selected: remove selection class
        Tobago.removeCssClass(row, "tobago-sheet-row-selected");

        if (img && (!selector || !selector.src.match(/Disabled/))) {
          img.src = this.uncheckedImage;
        }
      }
      else {  // selected: check selection class
        if (classes.search(/tobago-sheet-row-selected/) == -1) {
          Tobago.addCssClass(row, "tobago-sheet-row-selected");
        }
        if (img && (!selector || !selector.src.match(/Disabled/))) {
          img.src = this.checkedImage;
        }
      }
      row = Tobago.element(this.id + "_data_tr_" + ++i );
    }
  };

  this.toggleSelectionForRow = function(dataRow) {
    var rowIndex = dataRow.id.substring(dataRow.id.lastIndexOf("_data_tr_") + 9);
    this.toggleSelection(rowIndex);
  };

  this.toggleSelection = function(rowIndex) {
    this.tobagoLastClickedRowId
        = Tobago.element(this.id + "_data_tr_" + rowIndex).id;
    var selector = Tobago.element(this.id + "_data_row_selector_" + rowIndex);
    if (!selector || !selector.src.match(/Disabled/)) {
      var re = new RegExp("," + rowIndex + ",");
      if (this.selected.value.search(re) != -1) {
        this.selected.value = this.selected.value.replace(re, "");
      }
      else {
        this.selected.value = this.selected.value + "," + rowIndex + ",";
      }
    }
  };

  this.selectRange = function(dataRow) {
    var lastRow = Tobago.element(this.tobagoLastClickedRowId);
    var firstIndex = lastRow.id.substring(lastRow.id.lastIndexOf("_data_tr_") + 9) - 0;
    var lastIndex  = dataRow.id.substring(dataRow.id.lastIndexOf("_data_tr_") + 9) - 0;
    var start;
    var end;
    if (firstIndex > lastIndex) {
      start = lastIndex;
      end = firstIndex;
    }
    else {
      start = firstIndex;
      end = lastIndex;
    }
    for (var i = start; i <= end; i++) {
      var re = new RegExp("," + i + ",");
      if (this.selected.value.search(re) == -1) {
        var selector = Tobago.element(this.id + "_data_row_selector_" + i);
        if (!selector || !selector.src.match(/Disabled/)) {
          this.selected.value = this.selected.value + "," + i + ",";
        }
      }
    }
  };

  this.getFirstRow = function() {
    var element = Tobago.element(this.id + "_data_row_0_column0");// data div
    while (element && element.id.search(this.firstRowRegExp) == -1) {
//      LOG.debug("element id = " + element.id);
      element = element.parentNode;
    }
//    LOG.debug("element id = " + element.id);
    return element;
  };

  this.setupHeader = function() {
    var headerBox = Tobago.element(this.id + "_header_box_0");
    if (headerBox) {

      if (window.opera) {
        headerBox.style.marginLeft = "0px";
      }

      var width = headerBox.style.width;
      var tmpWidth = width.replace(/px/, "");
      headerBox.style.width = tmpWidth -0 + 10 + "px";
      headerBox.style.width = width;
    }
  };

  this.adjustResizer = function() {
    // opera needs this
    if (window.opera) {
      var position = 5;
      var index = 0;
      var resizer = Tobago.element(this.id + "_header_resizer_" + index);
      while (resizer) {
        resizer.style.right = - position;
        var headerBox = Tobago.element(this.id + "_header_box_" + index++);
        var width = headerBox.style.width.replace(/px/, "") - 0;
        position += width;
        resizer = Tobago.element(this.id + "_header_resizer_" + index);
      }
    }
  };

  this.adjustHeaderDiv = function () {
    this.contentTable.style.width = "10px";
    var contentWidth = this.contentDiv.style.width.replace(/px/, "") - 0;
    var clientWidth = this.contentDiv.clientWidth;
    var boxSum = 0;
    var idx = 0;
    var box = Tobago.element(this.id + "_header_box_" + idx++);
    while (box) {
      boxSum += (box.style.width.replace(/px/, "") - 0);
      box = Tobago.element(this.id + "_header_box_" + idx++);
    }
    if (clientWidth == 0) {
      clientWidth = Math.min(contentWidth, boxSum);
    }
    var minWidth = contentWidth - Tobago.Config.get("Sheet", "scrollbarWidth")
                   - Tobago.Config.get("Sheet", "contentBorderWidth");
    minWidth = Math.max(minWidth, 0); // not less than 0
    this.headerDiv.style.width = Math.max(clientWidth, minWidth);
    var fillBox = Tobago.element(this.id + "_header_box_filler");
    fillBox.style.width = Math.max(this.headerDiv.style.width.replace(/px/, "") - boxSum, 0);
    //  LOG.debug("adjustHeaderDiv(" + sheetId + ") : clientWidth = " + clientWidth + " :: width => " + headerDiv.style.width);
    //headerDiv.style.width = clientWidth;
    var clientWidth2 = this.contentDiv.clientWidth;
    if (clientWidth > clientWidth2) {
      // IE needs this
      this.headerDiv.style.width = Math.max(clientWidth2, minWidth);
      //    LOG.debug("second time adjustHeaderDiv(" + sheetId + ") : clientWidth2 = " + clientWidth2 + " :: width => " + headerDiv.style.width);
    }
    this.contentTable.style.width = this.contentDiv.clientWidth + "px";
    //  LOG.debug("div width   :" + contentDiv.clientWidth);
    //  LOG.debug("table width :" + contentTable.clientWidth);
    //  LOG.debug("boxSum      :" + boxSum);
    //  LOG.debug("filler      :" + fillBox.clientWidth);
    //  LOG.debug("fillerstyle :" + fillBox.style.width);
    //  LOG.debug("##########################################");
  };

  this.beginResize = function(event) {
    if (! event) {
      event = window.event;
    }
    this.resizerId = Tobago.element(event).id;
    if (this.resizerId) {
      this.oldX = event.clientX;
      var elementWidth = this.getHeaderBox().style.width;
      this.newWidth = elementWidth.substring(0,elementWidth.length-2);
    }
  };

  this.getHeaderBox = function() {
    var boxId = this.resizerId.replace(/_header_resizer_/, "_header_box_");
    return Tobago.element(boxId);
  };

  this.doResize = function(event) {
    if (! event) {
      event = window.event;
    }

    if (this.resizerId) {
      var box = this.getHeaderBox();
      var elementWidth = box.style.width;
      var elementWidthPx = elementWidth.substring(0,elementWidth.length-2);
      var divX = event.clientX - this.oldX;
      this.newWidth = elementWidthPx-0 + divX;
      if (this.newWidth < 10) {
        this.newWidth = 10;
      } else {
        this.oldX = event.clientX;
      }
      box.style.width = this.newWidth + "px";
    }
  };

  this.endResize = function(event) {
    if (! event) {
      event = window.event;
    }
    if (this.resizerId) {
      var columnNr
          = this.resizerId.substring(this.resizerId.lastIndexOf("_") + 1,
          this.resizerId.length);
      var idPrefix = this.id + "_data_row_";
      var idPostfix = "_column" + columnNr;
      var i = 0;
      var cell = Tobago.element(idPrefix + i++ + idPostfix);
      while (cell) {
        cell.style.width = this.newWidth + "px";
        cell = Tobago.element(idPrefix + i++ + idPostfix);
      }

      this.adjustScrollBars();
      this.adjustHeaderDiv();
      this.adjustResizer();
      this.storeSizes();
      delete this.resizerId;
    }
  };

  this.storeSizes = function() {
    var index = 0;
    var idPrefix = this.id + "_header_box_";
    var header = Tobago.element(idPrefix + index++);
    var widths = "";
    while (header) {
      width = header.style.width.replace(/px/, "");
      widths = widths + "," + width;
      header = Tobago.element(idPrefix + index++);
    }
    this.headerWidths.value = widths;
  };

  this.doScroll = function(event) {
    //LOG.debug("header / data  " + this.headerDiv.scrollLeft + "/" + this.contentDiv.scrollLeft);
    this.headerDiv.scrollLeft = this.contentDiv.scrollLeft;
    //LOG.debug("header / data  " + this.headerDiv.scrollLeft + "/" + this.contentDiv.scrollLeft);
    //LOG.debug("----------------------------------------------");
  };



  this.selectAll = function() {
    var row = this.firstRow;
    var i = this.firstRowIndex;
    while (row) {
      var selector = Tobago.element(this.id + "_data_row_selector_" + i);
      if (!selector || !selector.src.match(/Disabled/)) {
        var re = new RegExp("," + i + ",");
        if (this.selected.value.search(re) == -1) {
          this.selected.value = this.selected.value + "," + i + ",";
        }
      }
      row = Tobago.element(this.id + "_data_tr_" + ++i );
    }
    this.updateSelectionView();
  };

  this.unSelectAll = function() {
    var row = this.firstRow;
    var selector = Tobago.element(this.id + "_data_row_selector_" + i);
    if (selector) {
      var i = this.firstRowIndex;
      while (row) {
        selector = Tobago.element(this.id + "_data_row_selector_" + i);
        if (!selector || !selector.src.match(/Disabled/)) {
          var re = new RegExp("," + i + ",", 'g');
          this.selected.value = this.selected.value.replace(re, "");
        }
        row = Tobago.element(this.id + "_data_tr_" + ++i );
      }
    } else {
      this.selected.value = "";
    }
    this.updateSelectionView();
  };

  this.toggleAllSelections = function(sheetId) {
    var row = this.firstRow;
    var i = this.firstRowIndex;
    while (row) {
      this.toggleSelection(i);
      row = Tobago.element(this.id + "_data_tr_" + ++i );
    }
    this.updateSelectionView();
  };

  this.setup();

  LOG.debug("New Sheet with id " + this.id);
}
