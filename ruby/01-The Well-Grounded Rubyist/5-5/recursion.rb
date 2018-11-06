# 练习5-5 ： 递归中的局部变量


class C

	def x(value_for_a, recurse=false)
		a = value_for_a
		puts "Here's the inspect-string for 'self' : #{self}"
		puts "And here's 'a' : #{a}"

		if recurse
			puts "Calling myself (recursion) ..."
			x("Second value for a")
			puts "Back after recursion; here's 'a' : #{a}"
		end
	end

end


c = C.new
c.x("First value for a", true)