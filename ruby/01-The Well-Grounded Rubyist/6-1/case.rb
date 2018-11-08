# 练习6-1 ： 通过 case 解析用户输入


puts "Exit the program ? (yes or no) :"
# answer = gets.chomp		# gets 表示获取控制台输入， chomp 表示去掉输入内容末尾的换行符
answer = "yes"				# ST3 无法通过控制台输入， 这里写死变量值


case answer
	when "y", "yes"
		puts "Goodbye"
		exit
	when "no"
		puts "Ok, we'll continue"
	else
		puts "That's an unknow answer"
end

puts "Continue with the program"


