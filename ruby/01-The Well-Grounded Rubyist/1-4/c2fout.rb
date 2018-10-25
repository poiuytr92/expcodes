# 练习1-2 ： 摄氏-华氏度转换（读写文件版）

puts "Read celsius value from file ..."
value = File.read("temp.dat")
print "The celsius is : ", value, " .\n"

celsius = value.to_i	# 摄氏度
fahrenheit = celsius.to_i * 9 / 5 + 32	# 华氏度公式
print "The result is : ", fahrenheit, " .\n"

puts "Saving result to file ..."
fh = File.new("temp.out", "w")
fh.puts fahrenheit
fh.close