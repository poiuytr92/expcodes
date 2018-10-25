# 练习1-2 ： 摄氏-华氏度转换（IO交互版）
# 注：
# 	sublime text 的控制台不能实现输入流（即无法通过控制台输入）
# 	因此若在sublime text以 Ctrl+B 运行这段代码，因为 gets 无法输入的缘故会导致无法运行

puts "Please enter a celsius value : "
celsius = gets	# 摄氏度
fahrenheit = celsius.to_i * 9 / 5 + 32	# 华氏度公式
print "The result is : "	# print直接打印原生字符
print fahrenheit
puts " ."	# puts会在没有以换行符结束的字符串末尾添加换行符后再打印