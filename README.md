# FakePlayer - 假人插件

这个插件模拟出真实的玩家来保证区块的加载以及怪物的生成，生电服利器。

### 支持版本

`1.20.*` 的 paper, purpur _(建议)_

## 特性

1. 这个假人有点"真", 与真实玩家一致, 可以保持区块的刷新和怪物生成, 距离取决于服务器设置的模拟距离
2. 假人的原生数据档案、成就数据不会存档，但第三方的档案还会存在

## 命令

+ `/fp spawn [名称] [世界] [位置]` - 创建假人
+ `/fp kill` - 移除假人
+ `/fp list [页码] [数量]` - 查看所有假人
+ `/fp distance` - 查看与假人的距离
+ `/fp tp` - 传送到假人身边
+ `/fp tphere` - 将假人传送到身边
+ `/fp tps` - 与假人交换位置
+ `/fp config get <配置项>` - 查看配置项
+ `/fp config set <配置项> <配置值>` - 设置配置项
+ `/fp health` - 查看生命值
+ `/fp exp` - 查看经验值
+ `/fp expme` - 转移经验值
+ `/fp attack (once | continuous | interval | stop)` - 攻击/破坏
+ `/fp use (once | continuous | interval | stop)` - 使用/交互/放置
+ `/fp jump (once | continuous | interval | stop)` - 跳跃
+ `/fp drop [-a|--all]` - 丢弃手上物品
+ `/fp dropinv` - 丢弃背包物品
+ `/fp look (north | south | east| west | up | down | at | entity)` - 看向指定位置
+ `/fp turn (left | right | back | to)` - 转身到指定位置
+ `/fp move (forward | backward | left | right)` - 移动假人
+ `/fp ride (anything | normal | stop)` - 骑乘
+ `/fp cmd <假人> <命令>` - 执行命令
+ `/fp reload` - 重载配置文件

此外，假人是一个模拟玩家，因此可以被任何指令所识别比如 `kick`, `tp`, `ban` 等等

## 权限

**_默认所有权限是 op 拥有，请通过权限管理插件来分配！_**

| 节点                        | 指令                                                                                                                                                      |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| fakeplayer.spawn          | `spawn`, `list`, `kill`, `distance`, `dropinv`, `drop`                                                                                                  |
| fakeplayer.spawn.location | `spawn` 可以指定出生点                                                                                                                                         |
| fakeplayer.spawn.name     | `spawn` 可以自定义名称                                                                                                                                         |
| fakeplayer.tp             | `tp`, `tps`, `tphere`                                                                                                                                   |
| fakeplayer.profile        | `exp`, `health`                                                                                                                                         |
| fakeplayer.exp            | `expme`                                                                                                                                                 |
| fakeplayer.action         | `sneak`, `turn`, `jump`, `look`, `move`, `attack`, `use`, `ride`                                                                                        |
| fakeplayer.cmd            | `cmd`                                                                                                                                                   |
| fakeplayer.admin          | `reload`                                                                                                                                                |
| fakeplayer.alive._TIME_   | 假人存活时间。`TIME` 可以替换为 `15min`, `30min`, `1hour`, `2hour`, `4hour`, `8hour`, `12hour`, `24hour`, `permanent` 如 `fakeplayer.alive.1hour` 表示创建出来的假人存活 `1 小时` |
| 无                         | `config`                                                                                                                                                |

## 玩家个性化配置

此项是每位玩家对自己创建假人的个性化配置, 修改完配置后下一次创建假人将会生效

命令例子:

+ `/fp config get collidable`
+ `/fp config set collidable false`

| 配置项            | 备注                                    |
|----------------|---------------------------------------|
| collidable     | 是否开启碰撞箱                               |
| invulnerable   | 是否无敌模式                                |
| look_at_entity | 是否自动看向附近的实体(包括玩家), 可以配合 `attack` 自动打怪 |
| pickup_items   | 是否能够拾取物品                              |
| skin           | 是否使用你的皮肤                              |

# FAQ

## 跨服同步背包的一定要看

### 由于开启下线时丢弃背包物品导致的背包复制 bug
如果你开启了 `drop-inventory-on-quiting`，一定要验证是否会有复制物品 bug。
验证过程:
1. `drop-inventory-on-quiting` 设置为 `true`
2. 创建一名假人
3. 通过 `/kill` 之类的命令杀死假人，触发他下线
4. 此时假人会将背包的物品吐出来
5. 重新召唤这名假人（名称应当是一致），看看背包同步插件是否将它吐出来的东西又放回背包

如果你的服务器存在这个问题，那你不要开启 `drop-inventory-on-quiting` 这个功能


### 由于 BC 跨服同时存在相同 UUID 的假人导致的背包复制 bug
在 `0.1.8` 以前的版本，假人的 UUID 是通过名称生成的，这意味着跨服存在相同名称的假人将会具有相同的 UUID，这可能导致跨服同步背包导致物品复制，请尽快升级为 `0.1.8` 之后的版本。

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

## 每次假人生成控制台有报错日志

这可能是由于 `LuckPerm` 或者其他插件没有监测到假人登陆但检测到假人加入游戏而导致,
你可以忽略它或者也修改配置文件开启模拟登陆:

```yaml
simulate-login: true
```

## 用了假人之后 `ESS` 或者其他插件多了好多存档

由于假人确实是一个玩家, 因此他会触发第三方插件创建存档, 此插件暂时无法针对那么多插件逐个处理, 但是已经尽可能地减少多余的存档,
具体的方案是：

1. 通过假人的名称生成对应的 UUID, 同样的名称只会生成一份第三方存档
2. 不记录 `Minecraft` 自带的玩家存档和成就数据

此外, `plugin/fakeplayer` 目录下有一份 `used-uuid.txt` 文件记录着假人使用过的
UUID，你可以通过这份文件筛查来清理多余的第三方插件存档。这份文件会在服务器关闭时更新。

## BungeeCord 玩家切换服务器假人会下线吗？

如果你的服务器 `spigot.yml` 里的 `bungeecord` 设置值为 `true`, 那么此插件将会进行兼容,
只要玩家在任意一个服务器里游玩，即使切换服务器他创建的假人都不会触发`跟随下线`。

## 会支持 Folia 吗?

有计划，但可能比较久。之前尝试过但发现要兼容的内容有点多，持续关注一下～

----

## 配置项

这个不定时更新内容，具体以插件的 `config.yml 为准`

```yml
# 配置文件版本
# 不要修改这个值
version: 12

# 服务器最多存在多少个假人
# 默认: 1000
server-limit: 1000

# 每个玩家最多创建多少个假人
# 默认: 1
player-limit: 1

# 假人存活时间, 0 表示永久
# 单位: 分钟
keepalive: 0

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
follow-quiting: true

# 退出时是否丢弃背包物品
# 有跨服背包同步的谨慎开启, 需验证是否会导致物品复制
# 验证过程:
#   1. 创建假人
#   2. 通过 /kill 之类的命令杀死这个假人，假人死前会将背包物品丢弃出来
#   3. 重新召唤这个假人, 查看他的背包数据有没有被复制了一份
drop-inventory-on-quiting: true

# 如果启用, 则一个 IP 只能创建 `maximum` 个假人
# 能够避免玩家开小号疯狂创建假人
detect-ip: true

# 服务器最近 5 分钟平均 TPS 低于这个值清除所有假人
# 每 60 秒检测一次
# 默认: 0, 即不开启, 因为移除假人可能导致玩家红石机器出问题, 按需开启吧
kale-tps: 0

# 是否模拟登陆
# 真实的玩家登陆流程是 "预登陆" -> "登陆" -> "加入游戏", 而假人插件默认情况下跳过了前两个步骤直接加入游戏
# 有一些需要在 "登陆" 时生成玩家档案的插件发生异常比如 LuckPerms
# 如果服务器没有出现严重的错误不需要理会这些异常, 只是这些插件无法对假人进行操作而已
# 开启也不一定能解决所有问题, 也可能因为一些 "限制新加入玩家" 的插件而导致假人出现问题, 并且会创建更多的第三方插件数据
simulate-login: true

# 预准备命令
# 假人诞生时会以控制台的身份按顺序执行以下命令
# 你可以用这个来实现权限组的分配之类的命令
# 非正版服请避免写一些危险指令以防第三方插件没有清档被其他玩家以假人的身份登陆而利用
# 占位符:
#   %p: 假人名称
#   %u: 假人 uuid
#   %c: 创建者的名称
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
destroy-commands:
  - ''
  - ''

# 自执行命令
# 假人在诞生时会以自己的身份按顺序执行命令
# 你可以在这里做添加 /register 和 /login 命令来防止 `AuthMe` 等插件踢掉超时未登陆的玩家
self-commands:
  - ''
  - ''

# 允许玩家让假人执行的命令
# 在这里你可以放一些你服务器的命令，玩家就可以执行
# 例如添加 /sit 之后, 玩家可以通过 '/fp cmd myfakeplayer sit' 让假人坐下来
# ！！！注意: 在这里定义的命令, 不需要 fakeplayer.cmd 权限！！！
# ！！！注意: 给了 fakeplayer.cmd 命令，玩家就能够控制假人执行所有(有权限的)命令！！！
allow-commands:
  - ''
  - ''

# 检测更新
check-for-updates: true

```
