# 练习6-3 ： 异常抛出与捕获


# 自定义异常类（这是可选的，也可以用内置的异常类，如 ArgumentError）
class MyException < Exception
	# UNDO
end


# 模拟抛出异常的方法
def fussy_method(x)
	unless x < 10
		raise MyException, "I need a number under 10 "
	else
		puts "Number is #{x}"
	end
end


# 捕获并处理异常
begin
	fussy_method(22)
rescue MyException => e
	puts "That was not an acceptable number!"
	puts "Here's the backtrace for this exception:"
	puts e.backtrace
	puts "And here's the exception object's message:"
	puts e.message
ensure
	puts "fianlly"		# ensure 类比就是 java 的 finally 语法
end