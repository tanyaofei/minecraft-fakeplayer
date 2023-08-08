# FakePlayer - 假人插件

这个插件模拟出真实的玩家来保证区块的加载以及怪物的生成，生电服利器。

### 支持版本

`1.20.*` 的 paper, purpur _(建议)_

## 特性

1. 这个假人有点"真", 与真实玩家一致, 可以保持区块的刷新和怪物生成, 距离取决于服务器设置的模拟距离
2. 假人的原生数据档案、成就数据不会存档，但第三方的档案还会存在

## 命令

+ `/fp spawn [世界] [位置]` - 创建假人
+ `/fp kill [假人]` - 移除假人
+ `/fp list [页码] [数量]` - 查看所有假人
+ `/fp distance` - 查看与假人的距离
+ `/fp tp [假人]` - 传送到假人身边
+ `/fp tphere [假人]` - 将假人传送到身边
+ `/fp tps [假人]` - 与假人交换位置
+ `/fp config get <配置项>` - 查看配置项
+ `/fp config set <配置项> <配置值>` - 设置配置项
+ `/fp health [假人]` - 查看生命值
+ `/fp exp [假人]` - 查看经验值
+ `/fp expme [假人]` - 转移经验值
+ `/fp attack (once | continuous | interval | stop) [假人]` - 攻击/破坏
+ `/fp use (once | continuous | interval | stop) [假人]` - 使用/交互/放置
+ `/fp jump (once | continuous | interval | stop) [假人]` - 跳跃
+ `/fp drop [假人] [-a|--all]` - 丢弃手上物品
+ `/fp dropinv [假人]` - 丢弃背包物品
+ `/fp look (north | south | east| west | up | down | at) [假人]` - 看向指定位置
+ `/fp turn (left | right | back | to) [假人]` - 转身到指定位置
+ `/fp move (forward | backward | left | right) [假人]` - 移动假人
+ `/fp cmd <假人>` - 执行命令
+ `/fp reload` - 重载配置文件

此外，假人是一个模拟玩家，因此可以被任何指令所识别比如 `kick`, `tp`, `ban` 等等

## 权限

**_默认所有权限是 op 拥有，请通过权限管理插件来分配！_**

| 节点                             | 指令                                                         |
|--------------------------------|------------------------------------------------------------|
| fakeplayer.spawn               | `spawn`, `list`, `kill`, `distance`                        |
| fakeplayer.spawn.location      | `spawn` 可以指定出生点                                            |
| fakeplayer.tp                  | `tp`, `tps`, `tphere`                                      |
| fakeplayer.profile             | `exp`, `health`                                            |
| fakeplayer.exp                 | `expme`                                                    |
| fakeplayer.action              | `drop`, `dropinv`, `sneak`, `turn`, `jump`, `look`, `move` |
| fakeplayer.experimental.action | `attack`, `use`                                            |
| fakeplayer.cmd                 | `cmd`                                                      |
| fakeplayer.admin               | `reload`                                                   |
| 无                              | `config`                                                   |

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

## 已知问题

1. 与 `clearfog(清除迷雾)` 或者 `multiverse(多世界)` 可能存在不兼容, 会导致假人出生一刻被传送走, 但已针对这种情况特殊处理了

## FAQ

### 假人生成之后过了一会自动掉线

这可能是由于类似 `AutheMe` 等登陆插件探测到假人长时间没有登陆, 可以在配置文件里的 `self-commands`
里将注册登陆的指令放进去比如:

```yaml
# 注意留意密码不能设置太简单不然 AuthMe 会不给使用
self-commands:
  - '/register abc123! abc123!'
  - '/login abc123!'
```

### 每次假人生成控制台有报错日志

这可能是由于 `LuckPerm` 或者其他插件没有监测到假人登陆但检测到假人加入游戏而导致,
你可以忽略它或者也修改配置文件开启模拟登陆:

```yaml
simulate-login: true
```

### 用了假人之后 `ESS` 或者其他插件多了好多存档

由于假人确实是一个玩家, 因此他会触发第三方插件创建存档, 此插件暂时无法针对那么多插件逐个处理, 但是已经尽可能地减少多余的存档,
具体的方案是：

1. 通过假人的名称生成对应的 UUID, 同样的名称只会生成一份第三方存档
2. 不记录 `Minecraft` 自带的玩家存档和成就数据

此外, `plugin/fakeplayer` 目录下有一份 `used-uuid.txt` 文件记录着假人使用过的
UUID，你可以通过这份文件筛查来清理多余的第三方插件存档。这份文件会在服务器关闭时更新。

### BungeeCord 玩家切换服务器假人会下线吗？

如果你的服务器 `spigot.yml` 里的 `bungeecord` 设置值为 `true`, 那么此插件将会进行兼容,
只要玩家在任意一个服务器里游玩，即使切换服务器他创建的假人都不会触发`跟随下线`。

### 会支持 Folia 吗?

有计划，但可能比较久。之前尝试过但发现要兼容的内容有点多，持续关注一下～

----

## 配置项

这个不定时更新内容，具体以插件的 `config.yml 为准`

```yml
# 配置文件版本
# 不要修改这个值
version: 9

# 服务器最多存在多少个假人
# 默认: 1000
server-limit: 1000

# 每个玩家最多创建多少个假人
# 默认: 1
player-limit: 1

# 假人名称模版
# 如果为空, 则跟随创建者的名字并且加上后缀
# 如果为其他值, 则使用该模版并且加上后缀
# 注意:
#   1. 如果包含除 英文字母、数字、下划线以外的字符，原生自带命令将不被支持
#   2. 长度超过 16 位将会被截取
#   3. 不能以 - 开头
name-template: ''

# 跟随下线
# 假人创建者玩家下线时是否自动下线
# 如果玩家只是切换服务器, 那么不会触发跟随下线
follow-quiting: true

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
simulate-login: false

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
```
