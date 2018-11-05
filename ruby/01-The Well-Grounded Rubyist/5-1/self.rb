# 练习5-1 ： 通过 puts 测试 self 的指代对象


puts "Top : #{self}"


class C
	puts "Just started class C : #{self}"

	module M
		puts "Nexted module C::M : #{self}"
	end

	puts "Back in the outer level of C : #{self}"


	# 方法里面的 self 指代的就是具体某个实例的指针
	def m
		puts "Class C, methon m : #{self}"
	end

end


c = C.new
c.m
puts "That was a call to x by #{c}"