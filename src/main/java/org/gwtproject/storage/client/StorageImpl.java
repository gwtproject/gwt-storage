/*
 * Copyright 2011 Google Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static elemental2.dom.DomGlobal.window;

import org.gwtproject.event.shared.HandlerRegistration;

import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.webstorage.WebStorageWindow;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;

/**
 * This is the HTML5 Storage implementation according to the <a
 * href="http://www.w3.org/TR/webstorage/#storage-0">standard
 * recommendation</a>.
 *
 * <p>
 * Never use this class directly, instead use {@link Storage}.
 * </p>
 *
 * @see <a href="http://www.w3.org/TR/webstorage/#storage-0">W3C Web Storage -
 *      Storage</a>
 */
class StorageImpl {

  public static final String LOCAL_STORAGE = "localStorage";
  public static final String SESSION_STORAGE = "sessionStorage";

  protected static List<StorageEvent.Handler> storageEventHandlers;

  @JsFunction
  private interface NativeCallback {
    void onEvent(StorageEvent event);
  }

  protected static EventListener jsHandler;

  private static Map<String, elemental2.webstorage.Storage> nameToStorage =
      new HashMap<String, elemental2.webstorage.Storage>();
  static {
    nameToStorage.put(LOCAL_STORAGE, WebStorageWindow.of(window).localStorage);
    nameToStorage.put(SESSION_STORAGE, WebStorageWindow.of(window).sessionStorage);
  }

  /**
   * Handles StorageEvents if a {@link StorageEvent.Handler} is registered.
   */
  protected static final void handleStorageEvent(StorageEvent event) {
    if (!hasStorageEventHandlers()) {
      return;
    }
    for (StorageEvent.Handler handler : storageEventHandlers) {
      try {
        handler.onStorageChange(event);
      } catch (Throwable t) {
        GwtMigrationUtils.reportUncaughtException(t);
      }
    }
  }

  /**
   * Returns <code>true</code> if at least one StorageEvent handler is
   * registered, <code>false</code> otherwise.
   */
  protected static boolean hasStorageEventHandlers() {
    return storageEventHandlers != null && !storageEventHandlers.isEmpty();
  }

  /**
   * This class can never be instantiated by itself.
   */
  protected StorageImpl() {
  }

  /**
   * Registers an event handler for StorageEvents.
   *
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   * @return {@link HandlerRegistration} used to remove this handler
   */
  public HandlerRegistration addStorageEventHandler(
      final StorageEvent.Handler handler) {
    getStorageEventHandlers().add(handler);
    if (storageEventHandlers.size() == 1) {
      addStorageEventHandler0();
    }

    return new HandlerRegistration() {
      @Override
      public void removeHandler() {
        removeStorageEventHandler(handler);
      }
    };
  }

  /**
   * Removes all items in the Storage.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-clear">W3C Web
   *      Storage - Storage.clear()</a>
   */
  public void clear(String storage) {
    nameToStorage.get(storage).clear();
  };

  /**
   * Returns the item in the Storage associated with the specified key.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @return the value associated with the given key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-getitem">W3C Web
   *      Storage - Storage.getItem(k)</a>
   */
  public String getItem(String storage, String key) {
    return nameToStorage.get(storage).getItem(key);
  }

  /**
   * Returns the number of items in this Storage.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @return number of items in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-l">W3C Web
   *      Storage - Storage.length()</a>
   */
  public int getLength(String storage) {
    return nameToStorage.get(storage).getLength();
  };

  /**
   * Returns the key at the specified index.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param index the index of the key
   * @return the key at the specified index in this Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-key">W3C Web
   *      Storage - Storage.key(n)</a>
   */
  public String key(String storage, int index) {
    // few browsers implement retrieval correctly when index is out of range.
    // compensate to preserve API expectation. According to W3C Web Storage spec
    // <a href="http://www.w3.org/TR/webstorage/#dom-storage-key">
    // "If n is greater than or equal to the number of key/value pairs in the
    // object, then this method must return null."
    return (index >= 0 && index < nameToStorage.get(storage).getLength()) ?
        nameToStorage.get(storage).key(index) : null;
  };

  /**
   * Removes the item in the Storage associated with the specified key.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-removeitem">W3C
   *      Web Storage - Storage.removeItem(k)</a>
   */
  public void removeItem(String storage, String key) {
    nameToStorage.get(storage).removeItem(key);
  }

  /**
   * De-registers an event handler for StorageEvents.
   *
   * @see <a href="http://www.w3.org/TR/webstorage/#the-storage-event">W3C Web
   *      Storage - the storage event</a>
   * @param handler
   */
  public void removeStorageEventHandler(StorageEvent.Handler handler) {
    getStorageEventHandlers().remove(handler);
    if (storageEventHandlers.isEmpty()) {
      removeStorageEventHandler0();
    }
  }

  /**
   * Sets the value in the Storage associated with the specified key to the
   * specified data.
   *
   * @param storage either {@link #LOCAL_STORAGE} or {@link #SESSION_STORAGE}
   * @param key the key to a value in the Storage
   * @param data the value associated with the key
   * @see <a href="http://www.w3.org/TR/webstorage/#dom-storage-setitem">W3C Web
   *      Storage - Storage.setItem(k,v)</a>
   */
  public void setItem(String storage, String key, String data) {
    nameToStorage.get(storage).setItem(key, data);
  }

  protected void addStorageEventHandler0() {
    StorageImpl.jsHandler = new EventListener() {
      @Override public void handleEvent(Event event) {
        StorageImpl.handleStorageEvent(Js.<StorageEvent>uncheckedCast(event));
      }
    };
    window.addEventListener("storage",  jsHandler, false);
  }

  /**
   * Returns the {@link List} of {@link StorageEvent.Handler}s 
   * registered, which is never <code>null</code>.
   */
  protected List<StorageEvent.Handler> getStorageEventHandlers() {
    if (storageEventHandlers == null) {
      storageEventHandlers = new ArrayList<StorageEvent.Handler>();
    }
    return storageEventHandlers;
  }

  /**
   * Returns the {@link Storage} object that was affected in the event.
   * 
   * @return the {@link Storage} object that was affected in the event.
   */
  protected Storage getStorageFromEvent(StorageEvent event) {
    elemental2.webstorage.StorageEvent event1 = Js.uncheckedCast(event);
    if (event1.storageArea == nameToStorage.get("localStorage")) {
      return Storage.getLocalStorageIfSupported();
    } else {
      return Storage.getSessionStorageIfSupported();
    }
  }

  protected void removeStorageEventHandler0() {
    window.removeEventListener("storage",
      StorageImpl.jsHandler, false);
  }
}
