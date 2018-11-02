# 练习4-5 ： 重用栈模块


require_relative "stacklike"

# 占位符： 一个用于模拟货物的类
class Suitcase
end



# 货仓
class CargoHold

	include Stacklike

	def load_and_report(obj)
		print "Load object "
		puts obj.object_id
		add_to_stack(obj)
	end

	def unload
		take_from_stack
	end

end


ch = CargoHold.new
sc1 = Suitcase.new
sc2 = Suitcase.new
sc3 = Suitcase.new
ch.load_and_report(sc1)
ch.load_and_report(sc2)
ch.load_and_report(sc3)
first_unload = ch.unload
print "The first Suitcase unload from the plane is "
puts first_unload.object_id