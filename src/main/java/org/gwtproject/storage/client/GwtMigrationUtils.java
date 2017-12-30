/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gwtproject.storage.client;

import static elemental2.dom.DomGlobal.console;

/**
 * A set of methods used as a temporary substitution of static GWT.* method calls.
 *
 * The actual behaviour should be decided later.
 */
class GwtMigrationUtils {

  /**
   * This documentation is copied verbatim from GWT.reportUncaughtException.
   *
   * Reports an exception caught at the "top level" to a handler set via
   * {@link #setUncaughtExceptionHandler(UncaughtExceptionHandler)}. This is
   * used in places where the browser calls into user code such as event
   * callbacks, timers, and RPC.
   * <p>
   * If no {@code UncaughtExceptionHandler} is set, the exception is reported
   * to browser. Browsers usually log these exceptions to the JavaScript
   * console.
   */
  public static void reportUncaughtException(Throwable t) {
    console.log(t.toString());
  }
}
