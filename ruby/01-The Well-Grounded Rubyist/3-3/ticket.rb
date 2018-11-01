# 练习3-2 ： 通过 attr_* 读写器自动创建 getter/setter 方法

class Ticket

	attr_reader :venue, :date, :price
	attr_writer :price

	def initialize(venue, date)
		@venue = venue
		@date = date
	end

end


ticket = Ticket.new("Town hall", "11/12/13")
ticket.price = 63.00
puts "The ticket cost $#{"%.2f" % ticket.price}"
ticket.price = 72.00
puts "Whoops -- it just went up. It now cost $#{"%.2f" % ticket.price}"



# 等价方式
class Ticket2

	attr_reader :venue, :date
	attr_accessor :price		# 表示创建存取器（同时包含读写功能）

	def initialize(venue, date)
		@venue = venue
		@date = date
	end

end


# 等价方式
class Ticket3

	attr_reader :venue, :date
	attr :price, true			# attr一次只能创建一个存取器，其中读取器是默认的，写入器是可选的（true表示包含写入器功能）

	def initialize(venue, date)
		@venue = venue
		@date = date
	end

end
