Welcome to the Obsidian Suite GitHub Repository. The Obsidian Suite is an open source project for Minecraft modding, focusing on animation. The project is made of three parts; The API, The Animator, and The Overhaul (coming soon). 

For more information, check out the website: http://www.dabigjoe.com

[Contribution guidelines](CONTRIBUTING.md)  
[Code of conduct](CODE_OF_CONDUCT.md)

### Setup the Workspace (Eclipse Guide) ###

* Clone repository
* Run 'gradlew setupDecompWorkspace' in root directory. 
* Run 'gradlew eclipse' in root directory.
* Create a new Eclipse workspace on the '/eclipse' directory.
* Import existing projects by searching in the ObsidianSuite directory - should produce 3 suggestions (Animator, API and Mod)
	
### Compatability ###

Version Checker: http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2091981-version-checker-auto-update-mods-and-clean
	
### tcn2obj ###

The Obsidian Suite makes use of tcn2obj to convert Tabula/Techne models to Wavefront (.obj) models.  
Source: https://github.com/Thutmose/tbl2obj
