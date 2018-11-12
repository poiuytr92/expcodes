# 练习7-1 ： 自定义类比较器


class Bid

	include Comparable

	attr_accessor :estimate

	def <=>(other)
		if self.estimate < other.estimate
			-1	# 代表 小于
		elsif self.estimate > other.estimate
			1	# 代表 大于
		else
			0	# 代表 等于
		end
	end

end



b1 = Bid.new
b2 = Bid.new

b1.estimate = 100
b2.estimate = 105

puts b1 < b2


