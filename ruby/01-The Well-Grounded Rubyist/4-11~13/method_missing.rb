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
			attr = method[9..-1]	# 字符串截取，截取 all_with 后第一个字符到末尾
			if self.public_method_defined?(attr)
				PEOPLE.find_all do |person|
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


j = Person.new("John")
p = Person.new("Paul")
g = Person.new("George")
r = Person.new("Ringo")
j.has_friend(p)
j.has_friend(g)
g.has_friend(p)
r.has_hobby("rings")

Person.all_with_friends(p).each do |person|
	puts "#{person.name} is friends with #{p.name}"
end

Person.all_with_hobbies("rings").each do |person|
	puts "#{person.name} is into rings"
end