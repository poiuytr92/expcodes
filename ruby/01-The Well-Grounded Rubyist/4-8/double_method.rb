# 练习4-8 ： 模块和模块存在同名方法


module M
	def report
		puts "'report' method in module M"
	end
end

module N
	def report
		puts "'report' method in module N"
	end
end


class C
	include M
	include N	# 后加载的模块最先被引用
	include M	# 模块M已经混合过一次了，这次混合是不生效的，因此M不是最后被混合的模块
end



c = C.new
c.report



class D
	prepend M	# prepend之间的优先级和include之间的优先级是一样的
	prepend N	# 后加载的模块最先被引用
	prepend M	# 模块M已经混合过一次了，这次混合是不生效的，因此M不是最后被混合的模块
end

d = D.new
d.report



class E
	prepend M	# prepend优先与include
	include N	# 后加载的模块最先被引用
	include M	# 模块M已经混合过一次了，这次混合是不生效的，因此M不是最后被混合的模块
end

e = E.new
e.report