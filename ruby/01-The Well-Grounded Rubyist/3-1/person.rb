# 练习3-1 ： 实例变量的使用

class Person

	# 相当于构造函数， new 的时候被调用
	def initialize(name="")
		@name = name
	end

	def set_name(name)
		puts "Setting person's name..."
		@name = name		# @开头表示实例变量，@@开头表示类变量， $开头表示全局变量
	end

	def get_name()
		puts "Return the person's name"
		return @name
	end

	# Ruby的语法糖，在定义方法的时候允许以 = 结尾
	# 使用的时候有两种使用方式：
	# person.name=("...")
	# person.name = "..."
	# 后一种方法会被Ruby自动转换为前一种方法，这是一种特殊的setting方法
	def name=(name)
		@name = name
	end

	# 最简单的 getter 方法
	def name
		@name
	end

end


joe = Person.new
joe.set_name("Joe")
puts joe.get_name()


bill = Person.new("Bill")
puts bill.get_name


leon = Person.new
leon.name=("Leon")
puts leon.name


primo = Person.new
primo.name = "Primo"
puts primo.name