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

Tobago.TabGroup = {};

/**
 * Initializes the tab-groups.
 * @param elements  a jQuery object to initialize (ajax) or null for initializing the whole document (full load).
 */
Tobago.TabGroup.init = function (elements) {

  var $tabGroups = Tobago.Utils.selectWithJQuery(elements, ".tobago-tabGroup");
  var markupString = "selected";
  var markupCssClass = "tobago-tab-markup-selected";

  $tabGroups.each(function () {
    var $tabGroup = jQuery(this);
    var $hiddenInput = $tabGroup.find("> input[type=hidden]");
    var $tabContent = $tabGroup.find("> .tab-content");

    $tabGroup.find(".tobago-tabGroup-header:first .tobago-tab .nav-link:not(.disabled)").click(function () {
      var $navLink = jQuery(this);
      var $tab = $navLink.parent(".tobago-tab");
      var tabGroupIndex = $tab.attr("tabgroupindex");

      $hiddenInput.val(tabGroupIndex);

      if ($tabGroup.attr("switchtype") === "client") {

        //remove data-markup, markup-css-class and .active from tabs/tab-content
        $tabGroup.find(".tobago-tab .nav-link.active").each(function () {
          var $navLink = jQuery(this);
          var $tab = $navLink.parent(".tobago-tab");
          var $activeTabContent = $tabContent.find(".tobago-tab-content.tab-pane.active");

          Tobago.Utils.removeDataMarkup($tab, markupString);
          $tab.removeClass(markupCssClass);
          $navLink.removeClass("active");
          $activeTabContent.removeClass("active");
        });

        //add data-markup, markup-css-class and .active from tabs/tab-content
        Tobago.Utils.addDataMarkup($tab, markupString);
        $tab.addClass(markupCssClass);
        $navLink.addClass("active");
        $tabContent.find(".tobago-tab-content.tab-pane[tabgroupindex=" + tabGroupIndex + "]").addClass("active");
      }
    });
  });
};

Tobago.registerListener(Tobago.TabGroup.init, Tobago.Phase.DOCUMENT_READY);
Tobago.registerListener(Tobago.TabGroup.init, Tobago.Phase.AFTER_UPDATE);
