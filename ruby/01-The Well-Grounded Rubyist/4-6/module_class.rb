# 练习4-6 ： 证明模块的包含和继承


module M
	def report
		puts "'report' method in module M"
	end
end

class  C
	include M
end

class D < C
end


obj = D.new
obj.report