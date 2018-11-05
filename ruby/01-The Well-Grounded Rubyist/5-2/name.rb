# 练习5-2 ： self的隐式使用


class Person

	attr_accessor :first_name, :middle_name, :last_name

	def whole_name
		name = first_name + " "
		name << "#{middle_name} " if middle_name
		name << last_name
	end

end


david = Person.new
david.first_name = "David"
david.last_name = "Black"
puts "David's whole name : #{david.whole_name}"
david.middle_name = "Alan"
puts "David's whole name : #{david.whole_name}"
