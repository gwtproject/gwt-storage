# Gwt3-Storage

[![Build Status](https://travis-ci.org/avierax/gwt-storage.svg?branch=master)](https://travis-ci.org/avierax/gwt-storage)

Migration of gwt2-storage to ensure gwt3 compatibility.

## Roadmap

 1. ~~Migrate the current gwt-2.8.2 code and remove incompatible dependencies~~ [DONE]
 2. ~~Remove dependency on JavaScriptObject~~ [DONE]  
 3. ~~Remove all jsni constructions~~ [DONE]
 4. Add support for Native Events (pending issue from Gwt2)
 5. Optionally support receiving notifications of current session modifications 
 6. Add support for automated testing multiple browser sessions from Gwt Unit Test

## Known issues

### Issues inherited from gwt2-storage

 - Native events are not yet correctly supported, they are emulated.
 - Testing multiple browser sessions in a single Gwt Unit Test is not supported by Gwt. Is it?

### Issues resulting from migration

 - It's unknown what mechanism should be used to substitute `GWT.reportUncaughtException`. See 
  method `org.gwtproject.storage.client.StorageImpl.handleStorageEvent`
  