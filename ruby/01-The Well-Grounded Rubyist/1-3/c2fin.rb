# 练习1-2 ： 摄氏-华氏度转换（读写文件版）

puts "Read celsius value from file ..."
value = File.read("temp.dat")
print "The celsius is : ", value, "\n"

celsius = value.to_i	# 摄氏度
fahrenheit = celsius.to_i * 9 / 5 + 32	# 华氏度公式
print "The result is : "	# print直接打印原生字符
print fahrenheit
puts " ."	# puts会在没有以换行符结束的字符串末尾添加换行符后再打印