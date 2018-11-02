# 练习4-1 ： 栈模块（通过数组实现一个类似栈的行为）
# 这是被混合的栈模块文件


module Stacklike

	# ||= 可避免重复初始化stack
	# 确保第一次调用此方法时可以得到一个空数组
	# 之后再调用此方法则不会覆写这个数组
	def stack
		@stack ||= []		# 等价于 @stack = @stack || []
	end

	# 注意这里调用的是 stack方法 而不是 @stack变量, 
	# @stack是实例变量，无法在模块中直接使用，只有当模块被混合入到类中才具备意义
	def add_to_stack(obj)
		stack.push(obj)		
	end

	def take_from_stack
		stack.pop			# 注意这里调用的是 stack方法 而不是 @stack变量
	end

end
