# 练习9-3 ： 散列


#  构造散列（注意构造时若存在重复key则会报错，而非覆盖）
h = { "a" => "1", "b" => "2" }
p h

# 构造空散列，构造参数表示当散列找不到键时返回的默认值
h = Hash.new(22)
p h
p h["key"]

# 构造一个空散列， 与上一个做法区别在于
# 当散列 hash 找不到键值 key 时，会把  key = 22 添加到散列中
h = Hash.new { |hash, key| hash[key] = 22 }
p h
puts h["not_exist_key"]		# 此时 "not_exist_key" = 22 会被添加到散列，然后返回 22
p h


# 通过类似数组的形式构造散列，但元素个数必须是偶数，否则报错
h = Hash["key1", "val1", "key2", 2]
p h

# 可以给 Hash[] 传参一个二维数组， 其内每个一维数组均由2个元素组成（代表key, val）
# 这种方式等价于前一种方式
h = Hash[ [["key1", "val1"], ["key2", 2]] ]
p h



# 添加元素到散列 （此时重复的键不会报错，但会覆盖）
h.store("key2", "xyz")
h["new_key"] = "new_val"
p h

# 从散列中提取值
puts h["key1"]			# h[] 所查找的键不存在时返回 nil
puts h.fetch("key2")	# fetch 所查找的键不存在时抛出异常

# 从散列中获取多个值（返回数组）
p h.values_at("key1", "new_key")



