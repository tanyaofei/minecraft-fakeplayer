# FakePlayer - 假人插件

[English](README_en.md) | 简体中文

这个插件模拟出真实的玩家来保证区块的加载以及怪物的生成，生电服利器。

### 支持版本

仅支持 `Paper` 及其下游如 (`Purpur`) 核心，所有测试均在 `Purpur` 进行，因此 `Purpur` 的兼容性最高

要求使用 JAVA 21 及以上

+ 支持 `1.20`, `1.20.2`, `1.20.3`, `1.20.4`, `1.20.5`, `1.20.6`
+ 支持 `1.21`


## 特性

1. 你可以召唤假人来帮你保持区块加载、怪物刷新
2. 大部份命令都可以对这些假人生效，因为他们对于服务器来说就是"真实的"玩家, 如 `kick`, `ban`, `res padd`。
3. 你可以使用假人的背包来存放物品。
4. 你可以控制假人执行一些动作比如: 跳跃、攻击、进食、睡觉等等。不仅如此，你还可以将这些行为设置为周期性的。
5. 发挥你的想象～


## 前置插件:

- [CommandAPI](https://commandapi.jorel.dev)


## 配置文件
与其他插件不同，Fakeplayer 只会生成一份名为 `config.tmpl.yml` 的**模版**配置文件，你需要将它重命名为 `config.yml` 才能用作配置文件。这样的好处是升级的时候可以提前知道新的内容。

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
| /fp stop      | 停止所有行为    | fakeplayer.command.stop      |                         |
| /fp turn      | 转身        | fakeplayer.command.turn      |                         |
| /fp look      | 看向指定位置    | fakeplayer.command.look      |                         |
| /fp move      | 移动        | fakeplayer.command.move      |                         |
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
- fakeplayer.command.stop - 停止所有行为
- fakeplayer.config.replenish - 自动补货
- fakeplayer.config.replenish.chest - 自动补货时可以从附近箱子里补
- fakeplayer.config.autofish - 允许自动钓鱼

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


# 构建项目

看这个[指引](./BUILD.md)

