# 练习4-11~13 ： 利用super拦截Kernel的method_missing消息，并通过覆写扩展其功能



class Person

	PEOPLE = []		# 全局数组
	attr_reader :name, :hobbies, :friends

	def initialize(name)
		@name = name
		@hobbies = []
		@friends = []
		PEOPLE << self	# 当有 Person 实例初始化时，将其添加到全局数组中
	end

	def has_hobby(hobby)
		@hobbies << hobby
	end

	def has_friend(friend)
		@friends << friend
	end

	# m是当前调用的方法名， *args是方法参数
	def self.method_missing(m, *args)
		method = m.to_s

		# 在类Person中定义了两个实例变量 @friends 和 @hobbies
		# 不过没有定义方法 all_with_friends 和  all_with_hobbies
		# 因此一般情况下直接调用 all_with_friends 和  all_with_hobbies 是不可能的
		# 但是此处通过覆写 method_missing， 对 all_with_* 进行截断提取其中的 friends 和 hobbies 字符串
		# 然后反射到 @friends 和 @hobbies 实例变量进行间接操作
		if method.start_with?("all_with_")

			# 字符串截取，截取 all_with_ 后第一个字符到末尾
			# 亦即取到 friends 或 hobbies 字符串
			attr = method[9..-1]

			# 检查是否定义了名为 friends 或 hobbies 的方法
			# 这个方法其实就是已经通过 attr_reader 定义的 getter 方法, 这个方法与变量同名
			if self.public_method_defined?(attr)

				# find_all 表示从全局数组 PEOPLE 中筛选出所有符合条件的实例 person
				# 下面的用法等价于  PEOPLE.find_all { |person| person.send(attr).include?(args[0]) }
				# 其中 person.send(attr).include?(args[0]) 是筛选条件
				# 且由于 find_all 是这部分代码的最后一个语句，因此其筛选结果（数组）作为此方法 method_missing 的返回值
				PEOPLE.find_all do |person|

					# person.send(attr) 表示调用实例 person 中的 friends 或 hobbies 的 getter 方法
					# 其实就是得到了 @friends 或 @hobbies 的数组引用
					# 然后 include? 检查数据中是否包含 args[0]（亦即 all_with_* 方法的入参）
					person.send(attr).include?(args[0])
				end
			else
				raise ArgumentError, "Can't find #{attr}"
			end
		else
			super
		end
	end

end


john = Person.new("John")
paul = Person.new("Paul")
george = Person.new("George")
ringo = Person.new("Ringo")
john.has_friend(paul)
john.has_friend(george)
george.has_friend(paul)
ringo.has_hobby("rings")


# all_with_friends 会触发到 重载的 method_missing 的逻辑
Person.all_with_friends(paul).each do |person|
	puts "#{person.name} is friends with #{paul.name}"
end

# all_with_hobbies 会触发到 重载的 method_missing 的逻辑
Person.all_with_hobbies("rings").each do |person|
	puts "#{person.name} is into rings"
end


# 这个方法会抛出异常，原因是对 all_with_* 消息的处理不包含 childrens
# Person.all_with_childrens("child")

# 这个方法会调用内置的 method_missing 方法，
# 原因是不满足 all_with_* 消息前缀，不会在 Person 类中处理
# Person.test_method_missing("method_missing")