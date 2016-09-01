### Setup the Workspace ###

* Clone repository
* If you are going to use eclipse, you need to add a fresh eclipse folder to the workspace. 
	* Download the zip from http://www.mediafire.com/download/ll6e53zz9tgh09h/EmptyEclipseFolderForMC.zip
	* Extract into the workspace.
* Run `gradlew setupDecompWorkspace` and `gradlew idea` or `gradlew eclipse`
* If using eclipse, run `gradlew --refresh-dependencies eclipse`

### Branching ###

* Based on: http://nvie.com/posts/a-successful-git-branching-model/
* Include issue branches for bug fixes, called issue-1 etc.
* Use --no-ff please!

### Exporting ###

1. Change the version string in MCEA_Main to the new version number.
2. Change version in build.gradle file.
3. Final commit on working branch - 'bump version'
4. Tag on working branch.
5. Merge working branch with master/dev.
6. Delete working branch.
7. Push changes to repo (including tag).
8. Export mod (gradlew build) and add to exports folder.
9. Add to mcea.log on server.
10. Add new version to server.
11. Run addLatest.sh.
12. Check it works!