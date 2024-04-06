# FakePlayer - 假人插件

[English](README_en.md)

这个插件模拟出真实的玩家来保证区块的加载以及怪物的生成，生电服利器。

### 支持版本

仅支持 `Paper` 及其下游 `Purpur` 核心

+ 1.20 / 1.20.1
+ 1.20.2
+ 1.20.3 / 1.20.4 (_**未经过测试**_)

## 特性

1. 这个假人有点"真", 与真实玩家一致, 可以保持区块的刷新和怪物生成, 距离取决于服务器设置的模拟距离
2. 假人的原生数据档案、成就数据不会存档，但第三方的档案还会存在

## 前置

### 必须前置:

- CommandAPI

## 命令

| 命令            | 作用        | 权限                           | 备注                      |
|---------------|-----------|------------------------------|-------------------------|
| /fp spawn     | 召唤假人      | fakeplayer.command.spawn     |                         |
| /fp kill      | 杀死假人      | fakeplayer.command.kill      |                         |
| /fp killall   | 杀死服务器所有假人 | OP                           |                         |
| /fp select    | 选中假人      | fakeplayer.command.select    | 当玩家假人数量 >= 2 时才会出现      |
| /fp selection | 查看选中假人    | fakeplayer.command.selection | 当玩家假人数量 >= 2 时才会出现      |
| /fp list      | 查看已召唤的假人  | fakeplayer.command.list      |                         |
| /fp distance  | 查看与假人的距离  | fakeplayer.command.distance  |                         |
| /fp drop      | 丢弃手上一个物品  | fakeplayer.command.drop      |                         |
| /fp dropstack | 丢弃手上整组物品  | fakeplayer.command.dropstack |                         |
| /fp dropinv   | 丢弃背包所有物品  | fakeplayer.command.dropinv   |                         |
| /fp skin      | 复制玩家皮肤    | fakeplayer.command.skin      | 非在线玩家有 60 秒冷却           |
| /fp invsee    | 查看假人背包    | fakeplayer.command.invsee    | 玩家对假人右键同等效果             |
| /fp sleep     | 睡觉        | fakeplayer.command.sleep     |                         |
| /fp wakeup    | 起床        | fakeplayer.command.wakeup    |                         |
| /fp status    | 查看假人状态    | fakeplayer.command.status    |                         |
| /fp respawn   | 让死亡的假人复活  | fakeplayer.command.respawn   | 当服务器配置假人死亡时不踢出才会出现      |
| /fp tp        | 传送到假人身边   | fakeplayer.command.tp        |                         |
| /fp tphere    | 让假人传送到身边  | fakeplayer.command.tphere    |                         |
| /fp tps       | 与假人交换位置   | fakeplayer.command.tps       |                         |
| /fp set       | 更改假人的配置   | fakeplayer.command.set       |                         |
| /fp config    | 更改默认假人配置  | fakeplayer.command.config    |                         |
| /fp expme     | 吸收假人经验值   | fakeplayer.command.expme     |                         |
| /fp attack    | 攻击        | fakeplayer.command.attack    |                         |
| /fp mine      | 挖掘        | fakeplayer.command.mine      |                         |
| /fp use       | 使用/交互/放置  | fakeplayer.command.use       |                         |
| /fp jump      | 跳跃        | fakeplayer.command.jump      |                         |
| /fp turn      | 转身        | fakeplayer.command.turn      |                         |
| /fp look      | 看向指定位置    | fakeplayer.command.look      |                         |
| /fp move      | 移动        | fakeplayer.command.mvoe      |                         |
| /fp ride      | 骑乘        | fakeplayer.command.ride      |                         |
| /fp sneak     | 潜行        | fakeplayer.command.sneak     |                         |
| /fp swap      | 交换主副手物品   | fakeplayer.command.swap      |                         |
| /fp hold      | 手持对应快捷栏物品 | fakeplayer.command.hold      |                         |
| /fp cmd       | 让假人执行命令   | fakeplayer.command.cmd       | 不给权限的情况下，允许执行配置文件里定义的命令 |
| /fp reload    | 重载配置文件    | OP                           |                         |

_此外，假人是一个模拟玩家，因此可以被任何指令所识别比如 `kick`, `tp`, `ban` 等等_

## 权限

实际上每一个命令都有一个单独权限，如果你觉得这样子太麻烦了，你可以设置预定好的批量权限

### 最基本的命令组权限

`fakeplayer.spawn`
包含了以下权限:

- fakeplayer.command.spawn - 创建假人
- fakeplayer.command.kill - 杀死假人
- fakeplayer.command.list - 列出假人
- fakeplayer.command.distance - 查看距离
- fakeplayer.command.select - 选中假人
- fakeplayer.command.selection - 查看选中的假人
- fakeplayer.command.drop - 丢弃一个物品
- fakeplayer.command.dropstack - 丢弃整组物品
- fakeplayer.command.dropinv - 丢弃背包物品
- fakeplayer.command.skin - 复制皮肤
- fakeplayer.command.invsee - 查看背包
- fakeplayer.command.status - 查看状态
- fakeplayer.command.respawn - 复活假人
- fakeplayer.command.config - 设置默认设置
- fakeplayer.command.set - 设置假人设置

### 关于传送的组权限

`fakeplayer.tp`

包含了以下权限:

- fakeplayer.command.tp
- fakeplayer.command.tphere
- fakeplayer.command.tps

### 控制假人行动的权限

`fakeplayer.action`

包含了以下权限:

- fakeplayer.command.attack - 攻击
- fakeplayer.command.mine - 挖掘
- fakeplayer.command.use - 右键
- fakeplayer.command.jump - 跳跃
- fakeplayer.command.sneak - 潜行
- fakeplayer.command.look - 查看
- fakeplayer.command.turn - 转身
- fakeplayer.command.move - 移动
- fakeplayer.command.ride - 骑乘
- fakeplayer.command.swap - 交换主副手物品
- fakeplayer.command.sleep - 睡觉
- fakeplayer.command.wakeup - 起床
- fakeplayer.command.hold - 切换快捷栏
- fakeplayer.config.replenish - 自动补货
- fakeplayer.config.replenish.chest - 自动补货时可以从附近箱子里补

如果你服务器不限制玩家的各种命令，则可以直接使用这个
`fakeplayer.basic`

包含了所有安全的权限, 只有 `/fp cmd` 命令排除在外

## 交互

+ 对着假人右键可查看假人的背包

## 玩家个性化配置

此项是每位玩家对自己创建假人的个性化配置, 修改完配置后下一次创建假人将会生效

命令例子:

+ `/fp config list` - 查看所有个性化配置
+ `/fp config set collidable false` - 设置个性化配置

| 配置项            | 备注                                        |
|----------------|-------------------------------------------|
| collidable     | 是否开启碰撞箱                                   |
| invulnerable   | 是否无敌模式                                    |
| look_at_entity | 是否自动看向附近的可攻击的实体(包括玩家), 可以配合 `attack` 自动打怪 |
| pickup_items   | 是否能够拾取物品                                  |
| skin           | 是否使用你的皮肤                                  |
| replenish      | 是否自动补货                                    |

## 开发者的建议

1. 默认情况下假人是根据召唤者名称生成的，而 UUID 根据假人名称生成。不建议服务器设置 `name-template`
   ，因为这样假人就变成公共的了，玩家对其使用 `res` 等配置可能具有风险

2. 不建议直接给玩家 `fakeplayer.command.cmd` 权限，这样玩家可以执行任何假人有权限的命令。建议通过配置文件加入白名单命令来允许玩家执行有限的命令

# 常见问题（很重要务必要看）

### 由于 BC 跨服同时存在相同 UUID 的假人导致的背包复制 bug

在 `0.1.8` 以前的版本，假人的 UUID 是通过名称生成的，这意味着跨服存在相同名称的假人将会具有相同的
UUID，这可能导致跨服同步背包导致物品复制，请尽快升级为 `0.1.8` 之后的版本。

## 假人不吸引仇恨

默认情况下假人是开启无敌模式的, 需要玩家自行通过 `/fp config set invulnerable false` 关闭无敌模式才会吸引仇恨。
关闭之后他会收到饥饿和生命值影响, 你可能需要使用 `res` 或者信标保证假人的`饥饿值`和`生命值`

## 假人生成之后过了一会自动掉线

这可能是由于类似 `AutheMe` 等登陆插件探测到假人长时间没有登陆, 可以在配置文件里的 `self-commands`
里将注册登陆的指令放进去比如:

```yaml
# 注意留意密码不能设置太简单不然 AuthMe 会不给使用
self-commands:
  - '/register abc123! abc123!'
  - '/login abc123!'
```

## 如何可以让假人 /sit 坐下或执行命令

以下提供两种方式，但前提这些功能都是由别的插件提供的，本插件只是允许假人去执行对应的命令

### 方式一

直接给玩家 `fakeplayer.cmd` 权限, 这样玩家能让假人执行所有假人自己拥有权限的命令, 包括 `/sit`

### 方式二 (推荐)

不给玩家 `fakeplayer.cmd` 权限, 在配置文件中的 `allow-commands` 将 `/sit` 配置进去, 在这一项配置里配置的命令不需要权限。

```yaml
allow-commands:
  - '/sit'
```

无论上述哪种方式, 当玩家输入 '/fp cmd ' 的补全提示是玩家自己有权限的命令，但实际执行的时候只能执行假人用有权限的命令。

## 安全问题

### 玩家使用假人 UUID 登陆怎么办？

插件会记录假人使用过的 UUID，玩家使用这些 UUID 将会被拒绝登录。

### 玩家被假人占用了 UUID 导致无法上线怎么办

你可以按照以下步骤接触占用：

1. 通过 ES、CMI 等插件将假人背包、末影箱等物品取出，放好
2. 关服（关服的时候才会更新文件）, 并在之后删除 `plugins/fakeplayer/used-uuid.txt` **里对应的 UUID 的记录行**，
   _注意不是删除整个文件_
3. 开服
4. 让被占用的玩家尽快登录游戏，假人不会使用有游玩记录的 UUID
5. 让原来假人的召唤者找你拿回属于他的物品

### 跨服背包同步会不会被复制

最新版已修复所有已知的背包复制问题，即使假人的名称是一样的，在不同的服务器上他们的 UUID 是不一样的，因此不会认定为同一个玩家。

## BungeeCord 玩家切换服务器假人会下线吗？

如果你的服务器 `spigot.yml` 里的 `bungeecord` 设置值为 `true`, 那么此插件将会进行兼容,
只要玩家在任意一个服务器里游玩，即使切换服务器他创建的假人都不会触发`跟随下线

## 会支持 Folia 吗?

有计划，但可能比较久。之前尝试过但发现要兼容的内容有点多，持续关注一下～

## 维护计划

由于插件特性，无法实现多版本兼容，每一个版本都需要单独维护。但因作者精力有限（_不是肾虚_
），目前暂定仅跟随维护最新的两个核心版本。目前是 `1.20.1` 和 `1.20.2`。

----

## 配置项

这个不定时更新内容，具体以插件的 `config.yml 为准`

```yml
# 配置文件版本
# 不要修改这个值
# THE VERSION OF THIS CONFIG FILE, DO NOT MODIFY IT
version: 13

# 多国语言配置
# language
i18n:
  locale: zh

# 服务器最多存在多少个假人
# 默认: 1000
# Define the maximum number of fake players on the server at the same time
# default: 1000
server-limit: 1000

# 每个玩家最多创建多少个假人
# 默认: 1
# Define the maximum number of fake players each player can spawn
# Default: 1
player-limit: 1

# 假人存活时间, 0 表示永久
# 单位: 分钟
# Define the maximum lifespan of fake players
# Unit: minute, `0` represents permanent
lifespan: 0

# 假人默认名称模版
# 如果为空, 则跟随创建者的名字并且加上后缀
# 如果为其他值, 则使用该模版并且加上后缀
# 这个值仅在使用 spawn 命令时没有自定义名称时生效
# 占位符:
#   %c: 创建者名称
# 注意:
#   1. 如果包含除 英文字母、数字、下划线 以外的字符，将会导致原版及第三方的许多命令失效
#   2. 长度超过 16 位将会被截取
#   3. 不能以 - 开头
# Name template of fake players
# If this value is empty, fake players will use creators' name as their name and add a numerical suffix.
# Otherwise, fake players will use this value as their names and add a numerical suffix.
# placeholder:
#   %c: the name of the creator
# tips:
#   1. If this value contains characters other than alphabetic, numbers, and underscores, many vanilla commands will not be usable on them.
#   2. Characters longer than 16 characters will be truncated
#   3. Can not start with '-'
name-template: ''

# 假人自定义名称允许的字符
#  格式: 正则表达式
#  默认: '^[a-zA-Z0-9_]+$'
# 注意:
#   1. 如果允许了 英文字母、数字、下划线 以外的字符, 将会导致原版及第三方的许多命令失效
#   2. 自定义名称需要玩家具有 `fakeplayer.spawn.name` 权限
#   3. 如果你改了正则表达式, 请确保它以 `^` 开头并且以 `$` 结尾
name-pattern: '^[a-zA-Z0-9_]+$'

# 跟随下线
# 假人创建者下线时是否也跟着下线
# 如果玩家只是切换服务器, 那么不会触发跟随下线
# Define whether fake personas will be removed when the creator quited.
follow-quiting: true

# 退出时是否丢弃背包物品
# Define whether fake players will drop items from their inventory when they remove.
drop-inventory-on-quiting: false

# 是否保存玩家存档, 如果你将这个设置改成 false, 你可能还需要将 `drop-inventory-on-quiting` 设置为 `true` 以防玩家放置一些物品到假人的背包
# 默认: true
# Define whether persist player's data, if this value is `false`,
# you may need to set `drop-inventory-on-quiting` as `true` in case players will put some items in their inventory
# 默认: true
persistent-data: true

# 死亡时是否剔除游戏, 如果设置为 false, 那么玩家可以通过 /fp respawn 命令重生
# 默认: true
# Define whether kick the fake player who was dead, if this value is true, players can type command '/fp respawn' to respawn him
# Default: true
kick-on-dead: true

# 如果启用, 则一个 IP 只能创建 `player-limit` 个假人
# 能够避免玩家开小号疯狂创建假人
# Define whether to check if players with the same IP sharing `player-limit`
detect-ip: true

# 服务器最近 5 分钟平均 TPS 低于这个值清除所有假人
# 每 60 秒检测一次
# 默认: 0, 即不开启, 因为移除假人可能导致玩家红石机器出问题, 按需开启吧
# Server will detect tps every 5 minutes, if the average tps is lower than this value, all fake players will be removed
# Default: 0, means disabled
# Tips:
#    It's not recommended to enable this option, as it may cause the redstone machine to malfunction
kale-tps: 0

# 预准备命令
# 假人诞生时会以控制台的身份按顺序执行以下命令
# 你可以用这个来实现权限组的分配之类的命令
# 占位符:
#   %p: 假人名称
#   %u: 假人 uuid
#   %c: 创建者的名称
# Server will execute the following commands after the fake player was spawned
# You can add some commands to give them permission, such as '/lp user %p permission set xxx true'
# placeholder:
#    %p: the name of the fake player
#    %u: the uuid of the fake player
#    %c: the name of creator
preparing-commands:
  - ''
  - ''

# 假人销毁时执行的命令
# 与 `preparing-commands` 类似, 会在假人销毁时依次执行的命令
# 也许可以用来销毁第三方插件的档案?
# 占位符:
#   %p: 假人名称
#   %u: 假人 uuid
#   %c: 创建者的名称
# Server will execute the following commands before the fake player was quited(PlayerQuitEvent)
# you can add some commands to clean up data
# placeholder:
#    %p: the name of the fake player
#    %u: the uuid of the fake player
#    %c: the name of creator
destroy-commands:
  - ''
  - ''

# 自执行命令
# 假人在诞生时会以自己的身份按顺序执行命令
# 你可以在这里做添加 /register 和 /login 命令来防止 `AuthMe` 等插件踢掉超时未登陆的玩家
# The fake player will execute the following commands
# You can add some command to make him to login
# - '/register ANY_PASSWORD'
# - '/login ANY_PASSWORD'
self-commands:
  - ''
  - ''

# 检测更新
check-for-updates: true

# 允许玩家让假人执行的命令
# 在这里你可以放一些你服务器的命令，玩家就可以执行
# 例如添加 /sit 之后, 玩家可以通过 '/fp cmd myfakeplayer sit' 让假人坐下来
# ！！！注意: 在这里定义的命令, 不需要 fakeplayer.cmd 权限！！！
# ！！！注意: 给了 fakeplayer.cmd 命令，玩家就能够控制假人执行所有(有权限的)命令！！！
# Define which commands can be executed by `/fp cmd` without 'fakeplayer.cmd' permission
allow-commands:
  - ''
  - ''


```
