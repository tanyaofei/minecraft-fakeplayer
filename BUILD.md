# Build introduction

Here is a simple introduction lead you to build this project

## Build NMS Dependencies

Spigot NMS artifacts are not published to a public Maven repository, so build each
target version locally with [BuildTools](https://www.spigotmc.org/wiki/buildtools/):

```shell
java -jar BuildTools.jar --rev 1.21 --remapped
java -jar BuildTools.jar --rev 26.1.2
```

Minecraft 26.1 and 26.1.1 are binary-compatible with 26.1.2, so the 26.1 series
uses the 26.1.2 artifact. The 26.2 bridge reuses that implementation and does not
require a separate NMS artifact.

