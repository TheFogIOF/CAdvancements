# Custom Advancements

This mod allows you to add custom advancements to Minecraft using standard vanilla advancement JSON files. If you know how to make advancements for a datapack, you already know how to use this mod - just drop your JSON files into the config folder and they will load as regular advancements.

The mod is primarily intended for mod and modpack authors who want to ship advancements without creating a full datapack or including them inside a mod jar.

![Example with BetterAdvancements](https://cdn.modrinth.com/data/cached_images/f7585666bfba108ea1830099e1e0f77740cbd406.png)
## Features

### Custom advancements via config folder

Place your advancement JSON files into:

```
./config/cadvancements/advancements/<namespace>/<your_files>.json
```

*   `<namespace>` — the namespace used in the advancement ID (for example `mymod`, `custom`, `tutorial`).
*   `<your_files>.json` — an advancement file with **exactly the same structure as vanilla** (`display`, `criteria`, `requirements`, `rewards`, etc.).

### Soft-reload
You can update the achievements using the standard /reload command.

### Fully vanilla-compatible JSON format

Every field that works in a vanilla advancement works here. You can use vanilla triggers (`minecraft:inventory_changed`, `minecraft:player_killed_entity`, etc.), vanilla rewards (loot, experience, function), and vanilla display options (icon, title, description, frame, background, show_toast, announce_to_chat, hidden).

<details>
<summary>Example of a JSON advancement</summary>

```json
{
  "display": {
    "icon": { "item": "minecraft:stone" },
    "title": "Stone Age",
    "description": "Mine your first stone",
    "frame": "task",
    "show_toast": true,
    "announce_to_chat": true
  },
  "criteria": {
    "get_stone": {
      "trigger": "minecraft:inventory_changed",
      "conditions": {
        "items": [ { "items": ["minecraft:stone"] } ]
      }
    }
  },
  "requirements": [ ["get_stone"] ]
}
```
</details>

### BetterAdvancements compatibility

If [BetterAdvancements](https://modrinth.com/mod/better-advancements) is installed, you can extend your advancements with an optional `better_display` block that controls how the advancement is drawn in the advancement tree.

```json
"better_display": {
  "pos_x": 0,
  "pos_y": 0,
  "completed_icon_color": "#FFFFFF",
  "uncompleted_icon_color": "#FFFFFF",
  "completed_title_color": "#FFFFFF",
  "uncompleted_title_color": "#FFFFFF",
  "draw_direct_lines": false,
  "hide_lines": false
}
```

| Field | Type | Description |
|---|---|---|
| `pos_x` / `pos_y` | int | Position offset of the advancement icon relative to its default spot. |
| `completed_icon_color` | hex string | Icon color for completed advancements. |
| `uncompleted_icon_color` | hex string | Icon color for uncompleted advancements. |
| `completed_title_color` | hex string | Title color for completed advancements. |
| `uncompleted_title_color` | hex string | Title color for uncompleted advancements. |
| `draw_direct_lines` | boolean | Draw connection lines as straight lines instead of curved ones. (works with bugs) |
| `hide_lines` | boolean | Hide the connection lines entirely. |

The `better_display` block is ignored if BetterAdvancements is not installed.

### Client and server friendly

Advancements loaded from the config folder behave like regular advancements — they sync to clients, work with the vanilla advancement GUI, grant rewards, and can be used in `/advancement grant` and `/advancement revoke` commands.