# 铁砧自定义重命名

## 样式表达式

- 每一组表达式由`&{` `<exp>` `}`包裹 `<exp>`为样式表达式.
- 每一个`&{` `<exp>` `}`表达式都会影响后面的所有文本,直到出现下一个`&{` `<exp>` `}`表达式
- 每一组表达式`&{` `<exp>` `}`内的`<exp>`可以多个组合`&{` `<exp>` `,` `<exp>` `,` `<exp>` `}`,使用`,`分割

## 单个样式表达式

### 颜色表达式

#### RGB颜色表达式

- 由`#`开始
- 之后为`6`位`十六进制`数字组成,第1~2位为红色值,3~4为绿色,5~6为蓝色.范围`00~FF`
- 示例`&{#66CCFF}Test`

![color1.png](doc\anvil_custm_rename\color1.png)

##### RGB渐变表达式

- 由两个RGB颜色表达式使用`->`链接,使用RGB值渐变过渡,示例`&{#AE00FF->#00BBFF}Test`

![color2.png](doc\anvil_custm_rename\color2.png)

#### HSV颜色表达式

- 使用`[` `]`包裹
- 内部有三个数值,分别为`色相(Hue,范围0~360)`,`饱和度(Saturation,范围0~100)`,`明度(Value,范围0~100)`,使用` `空格分割
- 示例`&{[281 100 100]}Test`

![color3.png](doc\anvil_custm_rename\color3.png)

##### HSV渐变表达式

- 由两个HSV颜色表达式使用`->`链接,使用HSV值渐变过渡,示例`&{[281 100 100]->[196 100 100]}test`

![color4.png](doc\anvil_custm_rename\color4.png)

### 其他样式

| 样式  | 表达式符号                                 | 示例         | 效果                                                             |
|-----|---------------------------------------|------------|----------------------------------------------------------------|
| 下划线 | `l`,`underline`,`UNDERLINE`           | `&{l}Test` | ![underline.png](doc\anvil_custm_rename\underline.png)         |
| 删除线 | `s`, `strikethrough`, `STRIKETHROUGH` | `&{s}Test` | ![strikethrough.png](doc\anvil_custm_rename\strikethrough.png) |
| 加粗  | `b`, `bold`, `BOLD`                   | `&{b}Test` | ![bold.png](doc\anvil_custm_rename\bold.png)                   |
| 混淆  | `o`, `obfuscated`, `OBFUSCATED`       | `&{o}Test` | ![obfuscated.gif](doc\anvil_custm_rename\obfuscated.gif)       |
| 倾斜  | `i`,`italic`,`ITALIC`                 | `&{i}Test` | ![italic.png](doc\anvil_custm_rename\italic.png)               |

以上的几种样式都可以在前面添加`!`符号来清除该样式(铁砧会默认给修改的物品名添加倾斜,使用`&{!i}`可以清除掉倾斜效果)

使用`r`,`rest`,`REST`可以清除所有样式

### 传统Formatting

使用`$`开头接`Formatting`字符,[参考链接](https://www.digminecraft.com/lists/color_list_pc.php),示例`&{$c}`

### 多样式组合示例
`&{#FF0000->#00FF00,b,l,!i}斩龙剑`

![example1.png](doc\anvil_custm_rename\example1.png)