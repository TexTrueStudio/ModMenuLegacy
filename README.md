# ModMenu
~~Hard to be more descriptive than that.~~ It enriches the standard Minecraft menu with an interface displaying a one-dimensional array of modifications



A picture's worth 2 words

![](https://gitlab.com/ENDERZOMBI102/modmenu-legacy/-/raw/1.12.2/images/mods_menu_screenshot.png?inline=false "Mod Menu")

### Developers:
- Mod Menu is on maven at: https://maven.fabricmc.net/io/github/prospector/modmenu/
- The icon and the client-ness both come from the fabric.mod.json (as per the spec)
- Badges and parenting are specified in a custom object in your fabric.mod.json as such:
```json5
"custom": {
    "modmenu": {
        "badges": [] // available badges: library, deprecated
        "parent": { // may be the parent's mod id or an object that provides all necessary infos 
            "id": "awesome-parent",
            "name": "Awesome Parent",
            "description": "An awesome parent, what do else do you expect?",
            "icon": "assets/child/awesome_parent_icon.png",
            "badges": [ "client" ]
        }
    }
}
```
- Mod parenting is used to display a mod as a child of another one.
  This is meant to be used for mods divided into different modules.
  The following element in a fabric.mod.json will define the mod as a child of the mod 'flamingo':
```json5
"custom": {
    "modmenu": {
        "parent": "flamingo"
    }
}
```
- ModMenuAPI
    - To use the API, implement the ModMenuApi interface on a class and add that as an entry point of type "modmenu" in your fabric.mod.json as such:
  ```json5
  "entrypoints": {
	"modmenu": [ "com.example.mod.ExampleModMenuApiImpl" ]
  }
  ```
    - Features
        - Mods can provide a Screen factory to provide a custom config screen to open with the config button. Implement the `getConfigScreenFactory` method in your API implementation.
        - Mods can provide additional mods to be displayed on the mods list.
        - Mods can provide additional parenting rules.
