# 练习4-9 ： super越级查询方法


module M
	def report
		puts "'report' method in module M"
	end
end


class C
	include M
	def report
		puts "'report' method in class C"
		puts "About to trigger the next higher-up report method..."
		super		# 继续进行对 report 方法的下一次搜索并执行
		puts "Back from the 'super' call"
	end
end


c = C.new
c.report