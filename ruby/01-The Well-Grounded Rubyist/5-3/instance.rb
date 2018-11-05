# 练习5-3 ： 实例变量和 self 的关系



class C

	puts "Just inside class definition block. Here's self : #{self}"
	puts "And here's the instance variable @v, belonging to #{self} : "
	@v = "I am an instance variable at the top level of a class body."
	puts @v

	def show_var
		puts "Inside an instance method definition block. Here's self : #{self}"
		puts "And here's the instance variable @v, belonging to #{self} : "
		puts @v		# 方法内外的 @v 不是同一个变量
	end

end


c = C.new
puts "=================="
c.show_var