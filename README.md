### Setup the Workspace (Eclipse Guide) ###

* Clone repository
* Run '..\gradlew setupDecompWorkspace' in the Animator and Mod folders. 
	Must be ..\ since wrapper is only in root directory.
* Run '..\gradlew eclipse' in the Animator and Mod folders. 
	API is automatically taken care of through project dependencies.
* Create a new Eclipse workspace on the ObsidianSuite directory.
* Create a new API project, targeted at the API directory - it will create the project from an existing one.
* Create projects for the Animator and Mod.
* Create run directories in the Animator and Mod directories.
* Create run configs for Animator and Mod. Make working directory the run folder.
	https://bedrockminer.jimdo.com/modding-tutorials/set-up-minecraft-forge/set-up-advanced-setup/

### Exporting ###

* Update build.gradle for relevant export.
* Run '..\gradlew build' from repestive directory. 
	Note building the Animator and Mod will also build the API.
