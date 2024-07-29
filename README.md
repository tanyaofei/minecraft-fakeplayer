# FakePlayer

[简体中文](README_zh.md)

This is a plugin inspired by Carpet Mod which allows you to spawn fake players to keep chunk loading.

[Click me](https://youtu.be/NePaDz-P5nI) to visit a demo video.

## Supported Versions

Only supports [Paper](https://papermc.io) and [Purpur](http://purpurmc.org)

Required JAVA 21+

+ supports `1.20`, `1.20.2`, `1.20.3`, `1.20.4`, `1.20.5`, `1.20.6`
+ supports `1.21`

## Requirement plugins:

- [CommandAPI](https://commandapi.jorel.dev)

## Features

1. You can spawn some fake players that look like real to the server.
2. They can be recolonized by commands from other plugins, such as `/ban`, `/tp`, `/pay`
3. You can edit their inventory
4. You can have them perform actions like moving, jumping, attacking, and eating. What's better? You can make it periodical!
5. Let your imagination run wild, have them do more things.

## Commands

| Command       | Description                               | Permission                   | Note                                                               |
|---------------|-------------------------------------------|------------------------------|--------------------------------------------------------------------|
| /fp spawn     | Spawn a fake player                       | fakeplayer.command.spawn     |                                                                    |
| /fp kill      | Kill a fake player                        | fakeplayer.command.kill      |                                                                    |
| /fp killall   | Kill all fake players on the server       | OP                           |                                                                    |
| /fp select    | Select a fake player                      | fakeplayer.command.select    | Appears only when player spawned more then 1 fake players          |
| /fp selection | View selected fake player                 | fakeplayer.command.selection | Appears only when player spawned more then 1 fake players          |
| /fp list      | List spawned fake players                 | fakeplayer.command.list      |                                                                    |
| /fp distance  | Show distance to a fake player            | fakeplayer.command.distance  |                                                                    |
| /fp drop      | Drop held item                            | fakeplayer.command.drop      |                                                                    |
| /fp dropstack | Drop entire stack of the held item        | fakeplayer.command.dropstack |                                                                    |
| /fp dropinv   | Drop all items in the inventory           | fakeplayer.command.dropinv   |                                                                    |
| /fp skin      | Copy skin of another player               | fakeplayer.command.skin      | 60 seconds cooldown if copy from a offline player                  | 
| /fp invsee    | Open an inventory of a fake player        | fakeplayer.command.invsee    | Right-clicking on fake players has the same effect                 |
| /fp sleep     | Sleep                                     | fakeplayer.command.sleep     |                                                                    |
| /fp wakeup    | Wake up                                   | fakeplayer.command.wakeup    |                                                                    |
| /fp status    | Show player status                        | fakeplayer.command.status    |                                                                    |
| /fp respawn   | Respawn a dead fake player                | fakeplayer.command.respawn   | Appears only when server config does not kick on fake player death |
| /fp tp        | Teleport to a fake player                 | fakeplayer.command.tp        |                                                                    |
| /fp tphere    | Teleport a fake player to you             | fakeplayer.command.tphere    |                                                                    |
| /fp tps       | Swap positions with fake player           | fakeplayer.command.tps       |                                                                    |
| /fp set       | Change the configuration of a fake player | fakeplayer.command.set       |                                                                    |
| /fp config    | Change default configuration              | fakeplayer.command.config    |                                                                    |
| /fp expme     | Transfer exp of a fake player to you      | fakeplayer.command.expme     |                                                                    |
| /fp attack    | Attack                                    | fakeplayer.command.attack    |                                                                    |
| /fp mine      | Mine                                      | fakeplayer.command.mine      |                                                                    |
| /fp use       | Use/Interact/Place                        | fakeplayer.command.use       |                                                                    |
| /fp jump      | Jump                                      | fakeplayer.command.jump      |                                                                    |
| /fp turn      | Turn around                               | fakeplayer.command.turn      |                                                                    |
| /fp look      | Look at specified location                | fakeplayer.command.look      |                                                                    |
| /fp move      | Move                                      | fakeplayer.command.mvoe      |                                                                    |
| /fp ride      | Ride                                      | fakeplayer.command.ride      |                                                                    |
| /fp sneak     | Sneak                                     | fakeplayer.command.sneak     |                                                                    |
| /fp swap      | Swap main and off-hand items              | fakeplayer.command.swap      |                                                                    |
| /fp hold      | Hold corresponding hotbar item            | fakeplayer.command.hold      |                                                                    |
| /fp cmd       | Execute command                           | fakeplayer.command.cmd       |                                                                    |
| /fp reload    | Reload config file                        | OP                           |                                                                    |

_In addition, fake players are recognized by any command, such as `kick`, `tp`, `ban`, etc._

## Permissions

In fact, each command has its permission. But you still can use the following permission packs.

### Basic Command Group Permissions

`fakeplayer.spawn` includes the following permissions:

- fakeplayer.command.spawn - Create fake player
- fakeplayer.command.kill - Kill fake player
- fakeplayer.command.list - List fake players
- fakeplayer.command.distance - View distance
- fakeplayer.command.select - Select fake player
- fakeplayer.command.selection - View selected fake player
- fakeplayer.command.drop - Drop an item
- fakeplayer.command.dropstack - Drop entire stack of items
- fakeplayer.command.dropinv - Drop all inventory items
- fakeplayer.command.skin - Copy skin
- fakeplayer.command.invsee - View inventory
- fakeplayer.command.status - View status
- fakeplayer.command.respawn - Respawn fake player
- fakeplayer.command.config - Set default settings
- fakeplayer.command.set - Set fake player settings

### Teleportation Group Permissions

`fakeplayer.tp` includes the following permissions:

- fakeplayer.command.tp
- fakeplayer.command.tphere
- fakeplayer.command.tps

### Action Control Permissions

`fakeplayer.action` includes the following permissions:

- fakeplayer.command.attack - Attack
- fakeplayer.command.mine - Mine
- fakeplayer.command.use - Use
- fakeplayer.command.jump - Jump
- fakeplayer.command.sneak - Sneak
- fakeplayer.command.look - Look
- fakeplayer.command.turn - Turn
- fakeplayer.command.move - Move
- fakeplayer.command.ride - Ride
- fakeplayer.command.swap - Swap main and off-hand items
- fakeplayer.command.sleep - Sleep
- fakeplayer.command.wakeup - Wake up
- fakeplayer.command.hold - Switch hotbar
- fakeplayer.config.replenish - Auto-replenish
- fakeplayer.config.replenish.chest - Can replenish from nearby chests when auto-replenishing

If your server does not restrict various player commands, you can use this directly.
`fakeplayer.basic` includes all secure permissions, except for `/fp cmd` commands.

## Interaction

+ Right-clicking on a fake player allows you to view their inventory.

## Player Personalized Configuration

This is the personalized configuration of each player's created fake player. After modifying the configuration, the next time a fake player is created, it will take effect.

Command examples:

+ `/fp config list` - View all personalized configurations
+ `/fp config set collidable false` - Set personalized configuration

| Configuration Item | Note                                                                                                                                |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| collidable         | Whether collision box is enabled                                                                                                    |
| invulnerable       | Whether invincible mode is enabled                                                                                                  |
| look_at_entity     | Automatically look at nearby attackable entities (including players), can be combined with `attack` to automatically fight monsters |
| pickup_items       | Whether to pick up items                                                                                                            |
| skin               | Whether to use your skin                                                                                                            |
| replenish          | Whether to auto-replenish                                                                                                           |


# Custom Translation
1. Create a `message` folder in `plugins/fakeplayer`
2. Copy [this file](fakeplayer-core/src/main/resources/message/message.properties) to `message` folder
3. Rename the file to `message_language_region.properties` such as `message_en_us.properties`
4. Edit your `config.yml`, set `i18n.locale` to the name suffix which you just created such as `en_us`
5. Type `/fp reload-translation` to reload translation file. If you change `i18n.local`, you should `/fp reload` first

**Make sure the translation file is encoding with UTF-8**

# FAQs (Important - Must Read)


## lost connection: PacketEvents 2.0 failed to inject
Some plugin change the `Connection` of the fake player, You can set `prevent-kicking` to `ALWAYS` to solve it.
```yaml
# config.yml
prevent-kicking: ALWAYS
```



## Fake players do not attract aggression

By default, fake players are in invincible mode. Players need to manually turn off invincible mode with `/fp config set invulnerable false` to attract aggression. After turning it off, they will
receive hunger and health effects. You may need to use `res` or beacon to ensure the fake player's `hunger` and `health`.

## Fake players automatically log out after a while

This may be because plugins like `AutheMe` detect that fake players have not logged in for a long time. You can include the login command in the configuration file's `self-commands` to prevent the
plugin from kicking out players for being idle:

```yaml
# Note: You should use a complex password, or AuthMe may reject it
self-commands:
  - '/register abc123! abc123!'
  - '/login abc123!'
```
