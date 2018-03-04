from PIL import Image, ImageFilter
import tensorflow as tf
import matplotlib.pyplot as plt
import cv2

def imageprepare():
    """
    This function returns the pixel values.
    The imput is a png file location.
    """
    file_name='./test/4.png'#导入自己的图片地址
    #in terminal 'mogrify -format png *.jpg' convert jpg to png
    im = Image.open(file_name).convert('L')


    # im.save("./test/test-convert.png")
    # plt.imshow(im)
    # plt.show()
    tv = list(im.getdata()) #get pixel values

    #normalize pixels to 0 and 1. 0 is pure white, 1 is pure black.
    tva = [ (255-x)*1.0/255.0 for x in tv] 
    #print(tva)
    return tva



    """
    This function returns the predicted integer.
    The imput is the pixel values from the imageprepare() function.
    """

    # Define the model (same as when creating the model file)
result=imageprepare()
x = tf.placeholder(tf.float32, [None, 784])
W = tf.Variable(tf.zeros([784, 10]))
b = tf.Variable(tf.zeros([10]))

def weight_variable(shape):
  initial = tf.truncated_normal(shape, stddev=0.1)
  return tf.Variable(initial)

def bias_variable(shape):
  initial = tf.constant(0.1, shape=shape)
  return tf.Variable(initial)

def conv2d(x, W):
  return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME')

def max_pool_2x2(x):
  return tf.nn.max_pool(x, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME')   

W_conv1 = weight_variable([5, 5, 1, 32])
b_conv1 = bias_variable([32])

x_image = tf.reshape(x, [-1,28,28,1])
h_conv1 = tf.nn.relu(conv2d(x_image, W_conv1) + b_conv1)
h_pool1 = max_pool_2x2(h_conv1)

W_conv2 = weight_variable([5, 5, 32, 64])
b_conv2 = bias_variable([64])

h_conv2 = tf.nn.relu(conv2d(h_pool1, W_conv2) + b_conv2)
h_pool2 = max_pool_2x2(h_conv2)

W_fc1 = weight_variable([7 * 7 * 64, 1024])
b_fc1 = bias_variable([1024])

h_pool2_flat = tf.reshape(h_pool2, [-1, 7*7*64])
h_fc1 = tf.nn.relu(tf.matmul(h_pool2_flat, W_fc1) + b_fc1)

keep_prob = tf.placeholder(tf.float32)
h_fc1_drop = tf.nn.dropout(h_fc1, keep_prob)

W_fc2 = weight_variable([1024, 10])
b_fc2 = bias_variable([10])

y_conv=tf.nn.softmax(tf.matmul(h_fc1_drop, W_fc2) + b_fc2)

init_op = tf.initialize_all_variables()



"""
Load the model2.ckpt file
file is stored in the same directory as this python script is started
Use the model to predict the integer. Integer is returend as list.

Based on the documentatoin at
https://www.tensorflow.org/versions/master/how_tos/variables/index.html
"""
saver = tf.train.Saver()
with tf.Session() as sess:
    sess.run(init_op)
    saver.restore(sess, "./model/model.ckpt")#这里使用了之前保存的模型参数
    print ("Model restored.")


    # 定义获取最终解的公式
    # argmax返回的是索引值，返回矩阵每一行或者每一列的最大值的索引，
    # 当选择axis=1时,表示每一行的最大值; 0表示每列的最大值索引，

    #   看上面的例子，第一列最大值为10，为该列的第二个，所以索引为1，第二列，最大值为45。为第四个，所以索引为3，
    #   第三列最大值为78，为第四个，索引为3，最后一个最大值为66，为第三个，所以索引为2。
    #   当axis为1，就是比每一行的值，返回最大值在该行的索引，
    #   比如，第一行，最大值为4，为第四个，索引为3，第二行最大值为10，为第一个，索引为0，以此类推。
    prediction=tf.argmax(y_conv, 1)

    # 等价于： predint=sess.run(prediction, feed_dict={x: [result], keep_prob: 1.0})
    predint = prediction.eval(feed_dict={x: [result], keep_prob: 1.0}, session=sess)
    print('recognize result:', predint[0])
