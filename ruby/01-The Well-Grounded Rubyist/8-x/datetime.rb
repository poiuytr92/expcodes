# 练习8-4 ： 日期时间

require 'date'		# 提供 Date 与 DateTime 类
require 'time'		# 增强 Time 类

puts Date.parse("April 24 1705").england.strftime("%B %d %Y")
puts Date.today

puts Date.parse("03/6/9")		# 2位的年份在 0-68 之间，自动位移 2000
puts Date.parse("33/6/9")
puts Date.parse("68/6/9")
puts Date.parse("69/6/9")
puts Date.parse("70/6/9")
puts Date.parse("77/6/9")		# 2位的年份在 0-68 之外，自动位移 1900 (这是1970年系统时代开启导致的)


puts Date.parse("2003/6/9")
puts Date.parse("6/9/2003")			# ruby会尽可能自动识别年月日的位置
puts Date.parse("6/9/03")
puts Date.parse("November 2 2013")	# ruby 会尽可能正确地解析
puts Date.parse("Novemb 2 2013")
puts Date.parse("Nov 2 2013")
puts Date.parse("2 Nov 2013")



puts Time.new
puts Time.now
puts Time.at(100000000)		# 从 1970-01-01 00:00:00 之后经过的秒数
puts Time.mktime(2007, 10, 3, 14, 3, 6)
puts Time.parse("March 22, 1985, 10:35 PM")


puts DateTime.new(2009, 1, 2, 3, 4, 5)
puts DateTime.now


dt = DateTime.now
puts dt.year
puts dt.hour
puts dt.minute

d = Date.today
puts d.day
puts d.monday?
puts d.sunday?
puts d.leap?	# 查询是否为闰年


t = Time.now
puts t.month
puts t.sec
puts t.dst?		# 查询是否为夏令时, 仅时间对象可用


# 格式化时间对象为字符串
puts t.strftime("%m-%d-%y")
puts t.strftime("Today is %x")
puts t.strftime("Otherwise know as %d-%b-%Y")
puts t.strftime("Or even day %e of %B, %Y")
puts t.strftime("The time is %H:%M")


# 时间对象上的加减运算针对的是秒数
t = Time.now
puts t
puts t + 20
puts t - 30


# 日期时间对象上的加减运算针对的是天数
# 使用 << 和 >> 针对的是月份
d = DateTime.now
puts d
puts d + 20
puts d - 45
puts d >> 3
puts d << 2
puts d.next
puts d.next_year
puts d.next_month
puts d.next_day

d = Date.today
puts d
puts d + 20
puts d - 45
puts d >> 3
puts d << 2
# puts d.prev 		# 不存在此方法
puts d.prev_year
puts d.prev_month
puts d.prev_day


# upto / downto 表示在某个范围内迭代
next_week = d + 7
d.upto(next_week) { |date| puts "#{date} is a #{date.strftime("%A")}" }