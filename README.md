# ChiyoMixin

ChiyoMixin is a Minecraft server software that combines **[Chiyogami](https://github.com/bea4dev/Chiyogami)**,
a multithreaded fork of Paper, with **[PaperMixin](https://github.com/AzisabaNetwork/PaperMixin)**,
which lets you apply Mixins to the server.

- Base: Minecraft / Paper **1.21.11**
- Threading: Chiyogami, with additional optimizations
- Mixin: PaperMixin is bundled (no `-javaagent` needed)

## Distribution

The jar is a paperclip jar, just like Paper's. It does not contain the vanilla server;
on first start it downloads the vanilla server from Mojang and patches it locally
(this creates `cache/`, `versions/` and `libraries/`).

## Running

```bash
java -Xms4G -Xmx4G -jar ChiyoMixin-paperclip-1.21.11-R0.1-SNAPSHOT.jar --nogui
```

When started with `java -jar`, PaperMixin is loaded automatically through the jar's `Launcher-Agent-Class`.

- Put Mixin modules (jars) in `<server root>/modules/`. See `chiyomixin-mixin/example` for how to make one
- PaperMixin extracts its working files to `<server root>/.papermixin/`
- To start without Mixin, add `-Dchiyomixin.mixin.disable=true`
- Passing the jar explicitly as `-javaagent` as well does not load PaperMixin twice

### Docker / Kubernetes

CI publishes an image based on [itzg/minecraft-server](https://github.com/itzg/docker-minecraft-server)
to `ghcr.io/azisabanetwork/chiyomixin` on every push:
`1.21.11`, `1.21.11-<build>` and `latest` from `ver/1.21.11`; `<branch>` and `sha-<commit>` from every branch.

It runs with `TYPE=CUSTOM` and the ChiyoMixin jar baked in, so every itzg setting
(`EULA`, `MEMORY`, `JVM_OPTS`, `OPS`, ...) works as usual and the server root is `/data`.

```bash
docker run -it -p 25565:25565 -v ./server:/data -e EULA=TRUE -e MEMORY=4G ghcr.io/azisabanetwork/chiyomixin:1.21.11
```

On Kubernetes (or with itzg's Helm chart), use `ghcr.io/azisabanetwork/chiyomixin:<tag>` as the image
in place of `itzg/minecraft-server`. Do not set `TYPE` yourself.

The configuration file is `chiyomixin.yml`, and the API package is `world.chiyomixin`.
ChiyogamiLib (`world.chiyogami.chiyogamilib`) works as is.

## Building

Requires `git` and **JDK 21**.

```bash
./gradlew applyAllPatches
./gradlew createChiyoMixinJar
```

The distributable jar is `build/libs/ChiyoMixin-paperclip-<version>.jar`.

## Project layout

| Path | Contents |
|---|---|
| `chiyomixin-api/` | API (sources of the ChiyoMixin-specific API) |
| `chiyomixin-server/minecraft-patches/` | Patches to the Minecraft sources |
| `chiyomixin-server/paper-patches/` | Patches to the Paper sources |
| `chiyomixin-mixin/` | PaperMixin (included build, bundled into the jar) |

## License

GPLv3 (Paper / Chiyogami). For PaperMixin, see `chiyomixin-mixin/LICENSE`.
