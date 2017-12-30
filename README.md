# Gwt3-Storage

Migration of gwt2-storage to ensure gwt3 compatibility.

## Roadmap

 1. ~~Migrate the current gwt-2.8.2 code and remove incompatible dependencies~~
 2. ~~Remove dependency on JavaScriptObject~~  
 3. Remove all jsni constructions
 4. Add support for Native Events (pending issue from Gwt2)
 5. Optionally support receiving notifications of current session modifications 
 6. Add support for automated testing multiple browser sessions from Gwt Unit Test

## Known issues

### Issues inherited from gwt2-storage

 - Native events are not yet correctly supported, they are emulated.
 - Testing multiple browser sessions in a single Gwt Unit Test is not supported by Gwt