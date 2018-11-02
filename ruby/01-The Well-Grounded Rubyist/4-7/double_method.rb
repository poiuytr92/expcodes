# 练习4-7 ： 模块和类存在同名方法

# 当 类BankAccount 通过[include] 混合 模块InterestBearing时
# 类BankAccount中的方法是先被搜索的
# 因此最终执行的方法是 BankAccount.calculate_interest

# 当 类BankAccount2 通过[prepend] 混合 模块InterestBearing时
# 模块InterestBearing中的方法是先被搜索的
# 因此最终执行的方法是 InterestBearing.calculate_interest


module InterestBearing
	def calculate_interest
		puts "Placeholder! We're in [module InterestBearing]."
	end 
end

class BankAccount
	include InterestBearing		# 混合模块
	def calculate_interest
		puts "Placeholder! We're in [class BankAccount]."
		puts "And we're overriding the calculate_interest method..."
		puts "witch was defined in the [module InterestBearing]."
	end
end


ba = BankAccount.new
ba.calculate_interest




class BankAccount2
	prepend InterestBearing		# 前置模块
	def calculate_interest
		puts "Placeholder! We're in [class BankAccount]."
		puts "And we're overriding the calculate_interest method..."
		puts "witch was defined in the [module InterestBearing]."
	end
end

ba = BankAccount2.new
ba.calculate_interest