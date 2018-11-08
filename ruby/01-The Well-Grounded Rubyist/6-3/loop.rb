# 练习6-3 ： 自定义实现 times、each、map 等循环器


# ==================================================
# times 是 Integer 类的一个实例方法
# 其作用是根据 整数n 的数值运行 n次 代码块

puts "Default n.times : "
3.times { |i| puts "I'm on iteartion #{i}" }

class Integer

	def my_times
		c = 0	# 迭代计数器
		until c == self		# self 表示 整型n 自身的数值， 即 n
			yield(c)		# 把当前计数值传给代码块，并执行
			c += 1
		end
		self	# 返回 整型n
	end

end


puts "Custom n.my_times : "
3.my_times { |i| puts "I'm on iteartion #{i}" }





# ==================================================
# each 是集合对象的一个实例方法
# 其作用是把集合中的元素逐个取出并传递给代码块

puts "Default array.each : "
array = [1, 2, 3, 4]
array.each { |e| puts "The block just got handed #{e}" }

class Array

	def my_each1
		i = 0
		until i == self.size	# self.size 表示集合的代销
			yield(self[i])		# self[i] 表示某个元素
			i += 1
		end
		self
	end

	# 通过自定义的 my_times 去实现 my_each
	def my_each2
		self.size.my_times do |i|	# do ... end 均作为 my_times 的代码块
			yield self[i]
			i += 1
		end
		self
	end

end

puts "Custom array.my_each1 : "
array = [1, 2, 3, 4]
array.my_each1 { |e| puts "The block just got handed #{e}" }

puts "Custom array.my_each2 : "
array = [1, 2, 3, 4]
array.my_each2 { |e| puts "The block just got handed #{e}" }



# ==================================================
# map 是数组的一个实例方法
# 其作用是把数组中的元素逐个取出并传递给代码块
# 其与 each 的区别在于， each 返回的是集合自身， map 返回的是新数组


puts "Default array.map : "
names = ["David", "Alan", "Black"]
p names.map { |name| name.upcase }


class Array

	def my_map1
		i = 0
		ary = []
		until i == self.size
			ary << yield(self[i])	# 每个元素经过代码块处理后，放入新数组
			i += 1
		end
		ary
	end

	# 通过自定义的 my_each 去实现 my_map
	def my_map2
		ary = []
		self.my_each2 { |e| ary << yield(e) }
		ary
	end

end



puts "Custom array.my_map1 : "
names = ["David", "Alan", "Black"]
p names.my_map1 { |name| name.upcase }


puts "Custom array.my_map2 : "
names = ["David", "Alan", "Black"]
p names.my_map2 { |name| name.upcase }