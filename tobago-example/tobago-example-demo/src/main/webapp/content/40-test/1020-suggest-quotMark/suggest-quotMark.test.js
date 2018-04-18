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

QUnit.test("Basics: 'M'", function (assert) {
  var inputString = "M";
  var expectedLength = 3;

  assert.expect(expectedLength + 1);
  var done = assert.async();

  var $in = jQueryFrame("#page\\:mainForm\\:input\\:\\:field");
  var $suggestions = getSuggestions("#page\\:mainForm\\:input");

  $in.val(inputString).trigger('input');

  waitForAjax(function () {
    $suggestions = jQueryFrame($suggestions.selector);
    return $suggestions.length === expectedLength;
  }, function () {
    $suggestions = jQueryFrame($suggestions.selector);

    assert.equal($suggestions.length, expectedLength);
    for (i = 0; i < expectedLength; i++) {
      assert.ok($suggestions.eq(i).find("strong").text().toUpperCase().indexOf(inputString.toUpperCase()) >= 0);
    }

    done();
  });
});

QUnit.test("Basics: 'Ma'", function (assert) {
  var inputString = "Ma";
  var expectedLength = 2;

  assert.expect(expectedLength + 1);
  var done = assert.async();

  var $in = jQueryFrame("#page\\:mainForm\\:input\\:\\:field");
  var $suggestions = getSuggestions("#page\\:mainForm\\:input");

  $in.val(inputString).trigger('input');

  waitForAjax(function () {
    $suggestions = jQueryFrame($suggestions.selector);
    return $suggestions.length === expectedLength;
  }, function () {
    $suggestions = jQueryFrame($suggestions.selector);

    assert.equal($suggestions.length, expectedLength);
    for (i = 0; i < expectedLength; i++) {
      assert.ok($suggestions.eq(i).find("strong").text().toUpperCase().indexOf(inputString.toUpperCase()) >= 0);
    }

    done();
  });
});

QUnit.test("Basics: 'Mar'", function (assert) {
  var inputString = "Mar";
  var expectedLength = 2;

  assert.expect(expectedLength + 1);
  var done = assert.async();

  var $in = jQueryFrame("#page\\:mainForm\\:input\\:\\:field");
  var $suggestions = getSuggestions("#page\\:mainForm\\:input");

  $in.val(inputString).trigger('input');

  waitForAjax(function () {
    $suggestions = jQueryFrame($suggestions.selector);
    return $suggestions.length === expectedLength;
  }, function () {
    $suggestions = jQueryFrame($suggestions.selector);

    assert.equal($suggestions.length, expectedLength);
    for (i = 0; i < expectedLength; i++) {
      assert.ok($suggestions.eq(i).find("strong").text().toUpperCase().indexOf(inputString.toUpperCase()) >= 0);
    }

    done();
  });
});

QUnit.test("Basics: 'Mars'", function (assert) {
  var inputString = "Mars";
  var expectedLength = 1;

  assert.expect(expectedLength + 1);
  var done = assert.async();

  var $in = jQueryFrame("#page\\:mainForm\\:input\\:\\:field");
  var $suggestions = getSuggestions("#page\\:mainForm\\:input");

  $in.val(inputString).trigger('input');

  waitForAjax(function () {
    $suggestions = jQueryFrame($suggestions.selector);
    return $suggestions.length === expectedLength;
  }, function () {
    $suggestions = jQueryFrame($suggestions.selector);

    assert.equal($suggestions.length, expectedLength);
    for (i = 0; i < expectedLength; i++) {
      assert.ok($suggestions.eq(i).find("strong").text().toUpperCase().indexOf(inputString.toUpperCase()) >= 0);
    }

    done();
  });
});

function getSuggestions(id) {
  return jQueryFrame(Tobago.Utils.escapeClientId(
      jQueryFrame(id + " .tobago-suggest").attr("id") + "::popup") + " .tt-suggestion");
}
