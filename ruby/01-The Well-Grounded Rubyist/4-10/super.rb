# 练习4-10 ： 使用super在子类包装方法


class Bicycle
	attr_reader :gears, :wheels, :seats
	def initialize(gears = 1)
		@wheels = 2
		@seats = 1
		@gears = gears
	end
end


class Tandem < Bicycle
	def initialize(gears)
		super	# super 不带参数时，会自动向上传递子类的实参
		@seats = 2
	end
end



b = Bicycle.new
puts "Bicycle: #{"gears=%d" % b.gears}, #{"wheels=%d" % b.wheels}, #{"seats=%d" % b.seats}"

t = Tandem.new(3)
puts "Tandem: #{"gears=%d" % t.gears}, #{"wheels=%d" % t.wheels}, #{"seats=%d" % t.seats}"
