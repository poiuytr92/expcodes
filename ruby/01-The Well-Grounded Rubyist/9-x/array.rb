# 练习9-2 ： 数组


p Array.new
p Array.new(3)
p Array.new(3, "xyz")

n = 0
p Array.new(3) { n += 1; n * 10 }




# 由于 string 类默认不含有 to_ary 或 to_a 方法
# 因此若直接通过 Array(str) 构造数组，得到的是整个字符串作为数组中的一个元素
# 而非每个字符作为数组中的元素
str = "A string"
a_str = Array(str)
puts str.respond_to?(:to_ary)
puts str.respond_to?(:to_a)
p a_str		# ["A string"]


# 定义 string 类的方法 to_a
class String
	def to_a
		split(//)	# 通过正则切割字符串，这个正则意为切割每个字符
	end
end
a_str = Array(str)
p a_str		# ["A", " ", "s", "t", "r", "i", "n", "g"]




# 通过 %w 或 %W 构造字符串数组
# 这两种方式均会通过空格切割字符串每个单词作为数组每个元素
# 区别在于 %W 会运算插值语句
# 原因是 %w 解析字符串为单引号字符串， %W 解析字符串为双引号字符串
p %w{ David A. Black\ White #{2 + 2} }
p %W{ David A. Black\ White #{2 + 2} }


# 通过 %i 或 %I 构造符号数组
# 这两种方式均会通过空格切割字符串每个单词作为数组每个元素
# 区别在于 %I 会运算插值语句
# 原因是 %i 解析字符串为单引号字符串， %I 解析字符串为双引号字符串
d = "David"
p %i{ a b c #{d} }
p %I{ #{d} }




a = []
a[0] = "first"
p a
p a[1]
a.unshift("head")	# 在数组开头添加一个元素
a.push("tail1", "tail2", "tail3")		# 在数组末尾添加若干个元素
a << "tail"			# 在数组末尾添加一个元素
p a
a.shift()	# 移除数组开头一个元素
a.pop()		# 移除数组末尾一个元素
p a
a.shift(2)	# 移除数组开头2个元素
a.pop(2)	# 移除数组末尾2个元素
p a



a = [ "red", "orange", "yello", "purple", "gray", "indigo", "violet" ]
p a[3, 2]
a [3, 2] = [ "blue", "green" ]		# 替换数组元素
p a
p a.slice(1, 3)			# 从数组第 1 个位置开始提取后面 3 个元素
p a.values_at(0, 3, 4)	# 从数组中获取第 0、3、4 索引位置的元素



a = [1, 2, 3]
b = [4, 5, 6]
p a + b 		# a 与 b 均不会被改变， 两个数组的元素并集
p a.concat(b)	# a 会被修改, b 中各个元素合并到 a 中
p a.push(b)		# a 会被修改, b 整个数组作为一个元素添加到 a 中
p a << b 		# a 会被修改, b 整个数组作为一个元素添加到 a 中

a.replace(b)	# a 中所有元素被删除，然后替换成 b 中所有元素
p a

p [1, 2, 3, 4].reverse	# 数组反转


a = [1, 2, 3, [4, 5], 6, [7, [8], [9]]]
p a.flatten		# 数组扁平化 （默认是完全扁平化）


a = ["abc", 23, "def", [1, "234", ["kkk", 2]]]
puts a.join			# 串联数组中所有元素作为一个字符串（会自动对数组做扁平化）
puts a.join(",.,")	# 可以定义串联分隔符
puts a * " - "		# 另一种串联数组方式， 等价于 a.join(" - ")


a = [6, 1, 1, 2, 3, 4, 5, 5, 3, 4]
p a.uniq		# 数组去重


a = [6, 1, nil, 2, nil, 4, 5, 5, nil, 4]
p a.compact		# 去除数组中所有 nil 元素