# 练习3-2 ： 传统模式下的 getter/setter 方法

class Ticket

	def initialize(venue, date)
		@venue = venue
		@date = date
	end

	def price=(price)
		@price = price
	end

	def price
		@price
	end

	def venue
		@venue
	end

	def date
		@date
	end

end


ticket = Ticket.new("Town hall", "11/12/13")
ticket.price = 63.00
puts "The ticket cost $#{"%.2f" % ticket.price}"
ticket.price = 72.00
puts "Whoops -- it just went up. It now cost $#{"%.2f" % ticket.price}"