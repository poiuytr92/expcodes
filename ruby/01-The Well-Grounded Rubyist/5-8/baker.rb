# 练习5-8 ： 访问修饰符 private


# 蛋糕
class Cake

	def initialize(batter)
		@batter = batter	# （用鸡蛋、牛奶、面粉等调成的） 糊状物， 可理解成蛋糕的本质
		@baker = true
	end

end


# 鸡蛋
class Egg
	# None
end


# 面粉
class Flour
	# None
end


# 面包师
class Baker

	def bake_cake
		@batter = []	# （用鸡蛋、牛奶、面粉等调成的） 糊状物
		pour_flour()
		add_egg()
		stir_batter()
		return Cake.new(@batter)
	end

	# 倒面粉
	def pour_flour()
		@batter.push(Flour.new)
	end

	# 加鸡蛋
	def add_egg()
		@batter.push(Egg.new)
	end

	# 搅拌
	def stir_batter()
		# None
	end

	# 私有化方法（必须在方法被声明后才能使用 private ）
	private :pour_flour, :add_egg, :stir_batter

end




baker = Baker.new
baker.bake_cake