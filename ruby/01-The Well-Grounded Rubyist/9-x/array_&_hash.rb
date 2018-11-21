# 练习9-1 ： 数组与散列


array = [ "ruby", "diamond", "emerald" ]
hash = { 0 => "ruby", 1 => "diamond", 2 => "emerald" }
puts array[0]
puts hash[0]


# with_index 是一个整数计数器，它与散列的键值对保持一一对应关系
# 其中参数中的 (key, value) 是一个分离的数组，每个键值对都作为一个包含两个元素的数组使用
hash = { "red" => "ruby", "white" => "diamond", "green" => "emerald" }
hash.each.with_index do |(key, value), i|
	puts "Pair #{i} is #{key}/#{value}"
end

# 若把参数写成 key, value, i 的形式，
# 	则 key 会被绑定成为整个 [key, value] 数组
# 	   value 会绑定成为迭代器的计数值
# 	   i 则为 nil
hash.each.with_index do |key, value, i|
	puts "Pair #{value} is #{key[0]}/#{key[1]} (i is #{i})"
end