# 练习8-2 ： 符号


# 符号的不变性

puts "abc".object_id
puts "abc".object_id
puts :abc.object_id
puts :abc.object_id

puts Symbol.all_symbols.size			# 符号表
puts Symbol.all_symbols.grep(/abc/)		# 通过正则查询包含 abc 的符号
puts Symbol.all_symbols.include?(:xyz)	# 注意这种方式永远返回 true，因为 :xyz 在这个瞬间已经被定义且放到符号表了


d_hash = { :name => "David", :age => 55}
puts d_hash
puts d_hash[:age]

# 与前面是等价的（此为简写）
d_hash = { name: "David", age: 55}
puts d_hash
puts d_hash[:age]

# 与前面是不等价的（前面键值是符号，此处键值是字符串）
d_hash = { "name" => "David", "age" => 55}
puts d_hash
puts d_hash["name"]


