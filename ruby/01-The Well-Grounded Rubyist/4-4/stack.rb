# 练习4-4 ： 不使用模块直接实现栈


class Stack

	attr_reader :stack

	def initialize
		@stack = []
	end

	def add_to_stack(obj)
		@stack.push(obj)
	end

	def take_from_stack
		@stack.pop
	end

end



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