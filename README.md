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
+ `/fp look (north | south | east|  west | up | down | at) [假人]` - 看向指定位置
+ `/fp turn (left | right | back | to) [假人]` - 转身到指定位置
+ `/fp move (forward | backward | left | right) [假人]` - 移动假人
+ `/fp cmd <假人>` - 执行命令
+ `/fp reload` - 重载配置文件

此外，假人是一个模拟玩家，因此可以被任何指令所识别比如 `kick`, `tp`, `ban` 等等

## 权限

**_默认所有权限是 op 拥有，请通过权限管理插件来分配！_**

+ fakeplayer.spawn - `create`, `list`, `kill` 等使用命令权限
+ fakeplayer.tp - `tp`, `tps`, `tphere` 等传送指令权限
+ fakeplayer.profile - `exp`, `health`等查看假人信息命令权限 
+ fakeplayer.action - `drop`, `dropinv`, `invsee`, `sneak` 等控制命令权限
+ fakeplayer.exp - `expme` 命令权限
+ fakeplayer.cmd - `cmd` 命令权限


+ fakeplayer.experimental.action - `attack`, `use` 等控制命令权限 **(目前为实验性的, 未经过可靠性验证, 待成熟后将会移至 `fakeplayer.action`)**


+ fakeplayer.admin - 管理员权限

 
 
 _config 相关命令没有配置权限节点_

## 已知问题

1. 与 `clearfog` 清除迷雾插件不兼容, 会导致假人生成后被随机传送


## 配置项

这个不定时更新内容，具体以插件的 `config.yml 为准`

```yml
# 配置文件版本
# 不要修改这个值
version: 8

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

# 是否开启 bungeeCord 跟随下线
# 如果开启则玩家在切换服务器时不会因为在当前服务器下线而导致跟随下线
# 此配置仅在 `follow-quiting` 为 `true` 时生效
bungee: true

# 是否检测 IP
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
# 你可以在这里做一些 /register 之类的命令
self-commands:
  - ''
  - ''


```
