#! /usr/bin/env python
#coding=utf-8

''' Web界面-工具包 '''

# Template支持简单的字符串替换模板
from string import Template


# 返回用于创建CGI的 Content-type 行
def start_response(resp="text/html"):
    return ('Content-type: ' + resp + '\n\n')


# 返回用于创建HTML的开头页面代码
# 允许动态生成标题
def include_header(the_title):
    with open('templates/header.html') as headf:
        head_text = headf.read()
    header = Template(head_text)
    return header.substitute(title=the_title)

    
# 返回用于创建HTML的末尾页面代码
# 允许动态生成超链接（the_links是字典）
def include_footer(the_links):
    with open('templates/footer.html') as footf:
        foot_text = footf.read()
    link_string = ''
    for key in the_links:
        link_string += ('<a href="' + the_links[key] + 
                        '">' + key + '</a>&nbsp;&nbsp;&nbsp;&nbsp;')
    footer = Template(foot_text)
    return footer.substitute(links=link_string)


# 返回用于创建表单的开头代码
# 允许通过form_type方式把数据发送到the_url
def start_form(the_url, form_type="POST"):
    return ('<form action="' + the_url + '" method="' + form_type + '">')


# 返回用于创建表达的末尾代码
# 允许自定义提交按钮的文件submit_msg
def end_form(submit_msg="Submit"):
    return ('<p></p><input type="submit" value="' + submit_msg + '"></form>')


# 返回用于创建HTML单选按钮的代码
# 允许自定义按钮的名称rb_name和值rb_value
def radio_button(rb_name, rb_value):
    return ('<input type="radio" name="' + rb_name + 
            '" value="' + rb_value + '"> ' + rb_value + '</input><br/>')


# 返回用于创建HTML列表的代码
# 允许动态生成列表项items
def u_list(items):
    u_string = '<ul>'
    for item in items:
        u_string += ('<li>' + item + '</li>')
    u_string += '</ul>'
    return u_string


# 返回一个HTML标题标记
# 允许自定义标题（默认为2级标题）
def header(header_text, header_level=2):
    return ('<h' + str(header_level) + '>' + header_text + 
            '</h' + str(header_level) + '>')
            
# 返回一个HTML文本段
def para(para_text):
    return ('<p>' + para_text + '</p>')