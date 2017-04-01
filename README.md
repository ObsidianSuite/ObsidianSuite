### Setup the Workspace (Eclipse Guide) ###

* Clone repository
* Run 'gradlew setupDecompWorkspace' in root directory. 
* Run 'gradlew eclipse' in root directory.
* Create a new Eclipse workspace on the '/eclipse' directory.
* Import existing projects by searching in the ObsidianSuite directory - should produce 3 suggestions (Animator, API and Mod)

### Exporting ###

* Update build.gradle for relevant export.
* Run '..\gradlew build' from repestive directory. 
	Note building the Animator and Mod will also build the API.
