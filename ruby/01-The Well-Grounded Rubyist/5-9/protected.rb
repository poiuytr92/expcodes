# 练习5-9 ： 访问修饰符 protected


class C

	def initialize(n)
		@n = n
	end

	# 也可通过 attr_reader :n 自动生成 n 的 getter 方法
	def n
		@n
	end

	def compare(other)

		# 因为 方法n 是 protected 的，因此才能够在 self实例 中调用 other实例 中的 方法n
		# 若 方法n 声明为 private 则无法这样做
		if other.n > self.n
			puts "The other object's n is bigger"
		else
			puts "The other object's n is ths same or smaller"
		end
	end

	# 私有化方法（必须在方法被声明后才能使用 protected ）
	protected :n

end


c1 = C.new(100)
c2 = C.new(200)
c1.compare(c2)