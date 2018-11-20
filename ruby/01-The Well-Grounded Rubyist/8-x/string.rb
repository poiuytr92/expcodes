# 练习8-1 ： 字符串


puts " 双引号与单引号 ".center(50, "*")
puts "to do #{2 + 2}"		# 双引号字符串会自动计算插值表达式
puts 'not to do #{2 + 2}'	# 单引号字符串不会计算插值表达式


# 通过转义字符禁用双引号的插值表达式
puts " 转义字符 ".center(50, "*")
puts "not to do \#{2 + 2}"



puts " %q 与 %Q ".center(50, "*")
# 通过 %q 可以实现与单引号相同功能的字符串（当字符串中出现单引号时就可以不使用转义字符了）
# 通过 %Q 可以实现与双引号相同功能的字符串（当字符串中出现双引号时就可以不使用转义字符了）
# %q{...} 或 %Q{...} 后面可以使用除了 {} 以外的非字母数字的其他边界字符，只要配对、且不在字符串中出现即可
puts %q{not to do "#{2 + 2}"}
puts %Q{not to do "#{2 + 2}"}
puts %q-not to do "#{2 + 2}"-
puts %Q-not to do "#{2 + 2}"-
puts %q!not to do "#{2 + 2}"!
puts %Q!not to do "#{2 + 2}"!


puts " here文档 ".center(50, "*")
# here文档，对于多行字符串特别友好
# 通过 <<EOM 标识文档插入位置，接下来的N行都是here文档的内容，直到遇到以 EOM 起始的行结束
# 若通过 <<-EOM 标识插入位置，则不需要以 EOM 起始（即前面可以有空格，但必须独立一行）
here_text = "HEAD\n" + <<EOM + "TAIL"
This is the first line (contain "EOM")
This is the second line
Done
EOM
puts here_text


here_text = <<-EOM
This is the last line (contain "EOM")
This is the next line
      EOM
puts here_text



# 字符串索引/截取
puts " 字符串索引/截取 ".center(50, "*")
str = "Ruby is a cool language"
puts str[5]				# 字符串正向索引
puts str[-12]			# 字符串负向索引
puts str[5, 10]			# 字符串截取：从第5个字符开始（包含），取后面10个字符
puts str[5..10]			# 字符串截取：从第个字符开始（包含），到第10个字符为止（不包含）
puts str[5...10]		# 字符串截取：从第个字符开始（包含），到第10个字符为止（包含）


# 子串匹配
puts " 子串匹配 ".center(50, "*")
puts str["cool lang"]		# 包含子串：返回子串
puts str.slice("cool lang")	# 等价于前一句的 str["cool lang"]
puts str["very cool lang"]	# 不包含子串：返回 nil


# 正则匹配（通过 /.../ 包含正则表达式）
puts " 正则匹配 ".center(50, "*")
puts str[/c[ol ]+/]

# 修改子串
puts " 修改子串 ".center(50, "*")
str["Ruby"] = "Ybur"		
puts str
str[5] = "l"
puts str
str[6...10] = "ang"
puts str
puts str.replace("exp")		# 会把原串的所有内容替换为新值
puts str

# 删除子串
puts " 删除子串 ".center(50, "*")
str.slice!("cool lang")		# slice 方法不会作用到原串，而 slice! 则会修改原串
puts str
puts str.chomp("age")		# 删除末尾子串（默认为换行符 \n ），不会修改原串
puts str.chop				# 无条件删除末尾一个字符，不会修改原串
puts str


# 合并字符串
puts " 合并字符串 ".center(50, "*")
x = "abc"
y = "def"
puts "#{x}#{y}"	# 通过插值方式合并字符串
puts x + y		# 构造新的字符串 
puts x << y		# x 被改变



# 查找字符串
puts " 查找字符串 ".center(50, "*")
str = "Ruby is a cool language"
puts str.include?("cool")
puts str.start_with?("ruby")
puts str.end_with?("age")
puts str.empty?
puts "".empty?
puts " ".empty?
puts str.index("cool")	# 返回子串的索引位置（从左到右第一次出现的索引位置）
puts str.rindex("l")	# 返回子串的索引位置（从右到左第一次出现的索引位置）


# 字符串统计
puts " 字符串统计 ".center(50, "*")
puts str.size
puts str.count("a")		# 统计 a 出现的次数
puts str.count("g-m")	# 统计 g 到 m 之间的字母出现的次数（区分大小写）
puts str.count("A-Z")	# 统计 A 到 Z 之间的字母出现的次数（区分大小写）
puts str.count("aey. ")	# 统计某个字符集合中的字母出现的次数（类似正则的语法）
puts str.count("^aey. ")# 统计没有出现在某个字符集合中的字母出现的次数（类似正则的语法）
puts str.count("^g-m")	# 统计 g 到 m 之外的字母出现的次数（类似正则的语法）
puts str.count("ag-m", "^1")	# 可给定多组统计条件： 统计除了 1 之外的 a 和 g-m


# 删除字符（语法规则与 count 方法一致）
puts " 删除字符 ".center(50, "*")
puts str.delete("g-m")		# 不会作用到原串



puts " 字符的 ASCII 码值 ".center(50, "*")
# 获取字符的 ASCII 码值
puts "a".ord
puts "-".ord

# 获取 ASCII 码值对应的字符
puts 97.chr
# puts 11111.char	# 不存在的 ASCII 码会报错


# 字符串比较
puts " 字符串比较 ".center(50, "*")
puts "a" <=> "b"
puts "b" > "a"
puts "a" > "A"
puts "." > ","
puts "a" == "a"			# 测试两个对象的值是否相同
puts "a".eql?("a")		# 一般从结果上等价于 ==
puts "a".equal?("a")	# 测试两个是否为同一对象
puts "a".equal?(97)
puts "a".equal?(97.chr)


# 字符串转换
puts " 字符串转换 ".center(50, "*")
str = "david A. Black"
puts str.upcase		# 全大写
puts str.downcase	# 全小写
puts str.swapcase	# 大小写反转
puts str.capitalize	# 仅首字母大写


# 格式转换
puts " 格式转换 ".center(50, "*")
puts str.rjust(25)			# 居右（左填充直至长度为25）
puts str.ljust(25, ".")		# 居左（右填充直至长度为25）
puts str.center(25, "><")	# 居中

