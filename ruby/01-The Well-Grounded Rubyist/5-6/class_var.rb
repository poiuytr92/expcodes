# 练习5-6 ： 使用类变量持续追踪汽车的厂商数据


class Car

	@@makers = []	# 数组： 存储汽车的制造厂商		(类变量，类比Java的静态变量，但并非全局，只在类Car或其子类中可见)
	@@cars = {}		# 哈希表： 每个厂商拥有的汽车数量	(类变量，类比Java的静态变量，但并非全局，只在类Car或其子类中可见)
	@@total_count = 0	# 所有厂商名下的汽车总量		(类变量，类比Java的静态变量，但并非全局，只在类Car或其子类中可见)
	attr_reader :maker  # 当前汽车实例的所属厂商		(实例变量，类比Java的类成员变量)

	# 初始化汽车实例，并标识其所属的制造厂商
	def initialize(maker)
		if @@makers.include?(maker)
			puts "Creating a new car by #{maker} !"
			@maker = maker
			@@cars[maker] += 1
			@@total_count += 1
		else
			puts "No such maker : #{maker} ."
		end
	end

	# 查询当前汽车实例所属的厂商名下拥有的汽车数
	def maker_metas
		cnt = @@cars[self.maker]	# 这里的 self.maker 等价于 @maker , 都是实例变量
		return cnt ? cnt : 0
	end

	# （类比Java的静态方法）获取所有厂商名下的汽车总量
	def self.total_count	# 这里的 self.total_count 等价于 Car.total_count
		@@total_count
	end

	# （类比Java的静态方法）添加一个制造厂商
	def self.add_maker(maker)	# 这里的 self.add_maker 等价于 Car.add_maker
		unless @@makers.include?(maker)	# unless 等价于 if not
			@@makers << maker
			@@cars[maker] = 0
		end
	end

end



# 新增两个汽车厂商
Car.add_maker("Honda")
Car.add_maker("Ford")

# 制造汽车（并标识其所属的厂商）
hCar1 = Car.new("Honda")
fCar = Car.new("Ford")
hCar2 = Car.new("Honda")
unknow = Car.new("ABC")

puts "Counting cars of same maker as hCar2 ..."
puts "There are #{hCar2.maker_metas}"
puts "Total cars number is #{Car.total_count}"

