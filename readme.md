# Get By ID for Ignition Perspective

[![Ignition 8.3](https://img.shields.io/badge/Ignition-8.3-blue.svg)](https://www.docs.inductiveautomation.com/docs/8.3/intro)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.txt)

An Ignition Perspective module that lets scripts find components by their `meta.domId`, the same way `document.getElementById` works in JavaScript.

Instead of navigating the component tree by name:

```python
self.parent.getChild("FlexContainer").getChild("FlexContainer").getChild("example-button").props.text
```

give the component a `domId` and look it up directly:

```python
system.dom.getElementById("example-button").props.text
```

Renaming or moving the component no longer breaks the script.

## Functions

### `system.dom.getElementById(id)`

Returns the component in the **current view** whose `meta.domId` equals `id`, or `None` if there isn't one. Embedded views, docked views and popups are not searched.

### `system.dom.getPageElementById(id)`

Returns the component on the **whole page** whose `meta.domId` equals `id`, or `None`. Searches the current view first, then every other view on the page (embedded views, docked views and popups).

Both return the same kind of object as `self`, so `.props`, `.custom`, `.meta` and custom methods all work, including writes:

```python
lbl = system.dom.getElementById("statusLabel")
lbl.props.text = "Saved"
```

Both work in any Perspective script that runs within a view: component events, property change scripts, message handlers and custom methods. Called from elsewhere (gateway event scripts, the Script Console) they raise an error.

## Notes

- `domId` values should be unique within a view. If there are duplicates, the first match in document order wins; `getPageElementById` prefers matches in the calling view.
- Lookups scan the components in scope each time they're called, so a long `getChild` chain to a known location is cheaper. A missing id costs the most, since every component in scope is checked.

## Building

Requires Java 17, Node.js and Docker.

```bash
cp gradle.properties.template gradle.properties
./gradlew build
```

The unsigned module is written to `build/Get-By-ID.unsigned.modl`. Install it on an Ignition 8.3 gateway that allows unsigned modules.

## Development gateway

```bash
./gradlew build
docker compose -f docker/docker-compose.yml up -d
```

The gateway runs at http://localhost:9088 (`admin` / `password`) with the module installed. Ignition 8.3 loads modules only at startup, so after changing Java code rebuild and restart:

```bash
./gradlew build
docker compose -f docker/docker-compose.yml restart gateway
```

## Project layout

| Folder | Scope | Contents |
|---|---|---|
| `common` | Gateway + Designer | Script function signatures and docs |
| `gateway` | Gateway | `getElementById` / `getPageElementById` implementation, module hook |
| `designer` | Designer | Script autocomplete registration, module hook |
| `web` | Browser | Perspective component bundle |
| `docker` | – | Development gateway |

## License

MIT, see [LICENSE.txt](LICENSE.txt). Built from an MIT-licensed Perspective component module template.
