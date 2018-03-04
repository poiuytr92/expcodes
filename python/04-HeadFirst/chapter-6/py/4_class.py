#! /usr/bin/env python
#coding=utf-8

# 类


class Athlete:
    
    # 构造函数
    def __init__(self, value=0):
        self.thing = value  # 定义一个类属性
        
    def how_big(self):
        return len(self.thing)
    
# end class Athlete




# 为什么类中所有方法的第一个参数都必须是 self ？
a = Athlete("Holy Grail") # pyhton实际执行：Athlete.__init__(a, "Holy Grail")
print a.how_big()         # pyhton实际执行：Athlete.how_big(a)



        
        
    