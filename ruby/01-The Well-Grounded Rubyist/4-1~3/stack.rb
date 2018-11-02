# 练习4-2 ： 混合栈模块到栈类中


# 加载模块文件
require_relative "stacklike"

class Stack

	# 混合模块到类中
	include Stacklike

end




# 练习4-3 ： 测试栈的效果

s = Stack.new
s.add_to_stack("item1")
s.add_to_stack("item2")
s.add_to_stack("item3")
puts "Object currently on the stack:"
puts s.stack
taken = s.take_from_stack
puts "Remove top object:"
puts taken
puts "Now the stack:"
puts s.stack