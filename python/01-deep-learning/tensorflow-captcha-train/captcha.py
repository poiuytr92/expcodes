from PIL import Image
import numpy as np
import tensorflow as tf
from tensorflow.python.framework import graph_util
import random
import math

# 用于屏蔽警告：
#  Your CPU supports instructions that this TensorFlow binary was not compiled to use: AVX AVX2
# 该警告触发原因是 CPU是支持的，但tensorflow没有按照支持AVX、AVX2、FMA......编译造成的
import os
os.environ['TF_CPP_MIN_LOG_LEVEL']='2'


TRAIN_DIR = './data/train/'
TRAIN_CAPTCHA_NUM = 371  # 用于训练的验证码数量
TRAIN_MAX_COUNT = 10000    # 训练次数
MAX_ACC_LINE = 0.99    # 准确率合格线（达到则退出训练）

TEST_DIR = './data/test/'
TEST_CAPTCHA_NUM = 10  # 用于测试的验证码数量

PB_MODEL_PATH = './model/pb/captch.pb'
CKPT_MODEL_DIR = './model/ckpt/'
CKPT_MODEL_PATH = CKPT_MODEL_DIR + 'captch.ckpt'

IMAGE_WIDTH = 112
IMAGE_HEIGHT = 32
MAX_CAPTCHA = 5
CHAR_SET_LEN = 26 + 10  # 小写英文字符 + 数字（所训练的验证码暂时只有这两种组合）


def get_name_and_image(img_dir, img_num):
    all_image = os.listdir(img_dir)
    random_file = random.randint(0, img_num - 1)
    base = os.path.basename(img_dir + all_image[random_file])
    name = os.path.splitext(base)[0]
    image = Image.open(img_dir + all_image[random_file])
    image = np.array(image)
    return name, image


# 把图片文件名（5位验证码）转换成向量矩阵
# 矩阵维度为 5x36 （5行36列），每行代表一个字符，前26位映射到26个英文字母，后10位映射到10个数字
def name2vec(name):
    vector = np.zeros(MAX_CAPTCHA * CHAR_SET_LEN)
    for i, c in enumerate(name):
        offset = i * CHAR_SET_LEN
        if c.isdigit():
            offset = offset + 26
            idx = offset + ord(c) - 48
        else:
            idx = offset + ord(c) - 97

        vector[idx] = 1
    return vector


# 把向量矩阵还原成5位验证码（即图片文件名）
def vec2name(vec):
    name = []
    for i in vec:
        ch = chr(i + 97) if i < 26 else chr(i - 26 + 48)
        name.append(ch)
    return "".join(name)


# 生成一个训练batch
def get_next_batch(batch_size=64):
    batch_x = np.zeros([batch_size, IMAGE_HEIGHT*IMAGE_WIDTH])
    batch_y = np.zeros([batch_size, MAX_CAPTCHA*CHAR_SET_LEN])

    for i in range(batch_size):
        name, image = get_name_and_image(TRAIN_DIR, TRAIN_CAPTCHA_NUM)
        batch_x[i, :] = 1 * (image.flatten())
        batch_y[i, :] = name2vec(name)
    return batch_x, batch_y

####################################################

X = tf.placeholder(tf.float32, [None, IMAGE_HEIGHT * IMAGE_WIDTH], name='image_input')
Y = tf.placeholder(tf.float32, [None, MAX_CAPTCHA * CHAR_SET_LEN], name='mid_output')
keep_prob = tf.placeholder(tf.float32, name='keep_prob') # 神经元被选中的概率，用于防过拟合，取值0~1.0，值越大训练越快，但准确率越低


# 定义CNN卷积模型
def crack_captcha_cnn(w_alpha=0.01, b_alpha=0.1):
    x = tf.reshape(X, shape=[-1, IMAGE_HEIGHT, IMAGE_WIDTH, 1])
    # 3 conv layer
    w_c1 = tf.Variable(w_alpha * tf.random_normal([5, 5, 1, 32]))
    b_c1 = tf.Variable(b_alpha * tf.random_normal([32]))
    conv1 = tf.nn.relu(tf.nn.bias_add(tf.nn.conv2d(x, w_c1, strides=[1, 1, 1, 1], padding='SAME'), b_c1))
    conv1 = tf.nn.max_pool(conv1, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME')
    conv1 = tf.nn.dropout(conv1, keep_prob)

    w_c2 = tf.Variable(w_alpha * tf.random_normal([5, 5, 32, 64]))
    b_c2 = tf.Variable(b_alpha * tf.random_normal([64]))
    conv2 = tf.nn.relu(tf.nn.bias_add(tf.nn.conv2d(conv1, w_c2, strides=[1, 1, 1, 1], padding='SAME'), b_c2))
    conv2 = tf.nn.max_pool(conv2, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME')
    conv2 = tf.nn.dropout(conv2, keep_prob)

    w_c3 = tf.Variable(w_alpha * tf.random_normal([5, 5, 64, 64]))
    b_c3 = tf.Variable(b_alpha * tf.random_normal([64]))
    conv3 = tf.nn.relu(tf.nn.bias_add(tf.nn.conv2d(conv2, w_c3, strides=[1, 1, 1, 1], padding='SAME'), b_c3))
    conv3 = tf.nn.max_pool(conv3, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME')
    conv3 = tf.nn.dropout(conv3, keep_prob)

    # Fully connected layer
    w3 = math.ceil(IMAGE_WIDTH / 8) # 在全连接层中，验证码图片已经经过了3层池化层，使得长宽都压缩了8倍
    h3 = math.ceil(IMAGE_HEIGHT / 8)
    w_d = tf.Variable(w_alpha * tf.random_normal([w3 * h3 * 64, 1024]))
    b_d = tf.Variable(b_alpha * tf.random_normal([1024]))
    dense = tf.reshape(conv3, [-1, w_d.get_shape().as_list()[0]])
    dense = tf.nn.relu(tf.add(tf.matmul(dense, w_d), b_d))
    dense = tf.nn.dropout(dense, keep_prob)

    w_out = tf.Variable(w_alpha * tf.random_normal([1024, MAX_CAPTCHA * CHAR_SET_LEN]))
    b_out = tf.Variable(b_alpha * tf.random_normal([MAX_CAPTCHA * CHAR_SET_LEN]))
    out = tf.add(tf.matmul(dense, w_out), b_out, name='final_output')
    return out


# 训练
def train_crack_captcha_cnn():
    output = crack_captcha_cnn()

    loss = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=output, targets=Y))
    optimizer = tf.train.AdamOptimizer(learning_rate=0.001).minimize(loss)

    predict = tf.reshape(output, [-1, MAX_CAPTCHA, CHAR_SET_LEN])
    max_idx_p = tf.argmax(predict, 2)
    max_idx_l = tf.argmax(tf.reshape(Y, [-1, MAX_CAPTCHA, CHAR_SET_LEN]), 2)
    correct_pred = tf.equal(max_idx_p, max_idx_l)
    accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))

    saver = tf.train.Saver()
    with tf.Session() as sess:
        sess.run(tf.global_variables_initializer())

        for step in range(TRAIN_MAX_COUNT):
            batch_x, batch_y = get_next_batch(64)
            _, loss_ = sess.run([optimizer, loss], feed_dict={X: batch_x, Y: batch_y, keep_prob: 0.5})
            print("setp=%d, lost=%f" % (step, loss_))

            # 每100 step计算一次准确率
            if step % 100 == 0:
                batch_x_test, batch_y_test = get_next_batch(100)
                acc = sess.run(accuracy, feed_dict={X: batch_x_test, Y: batch_y_test, keep_prob: 1.0})
                print("setp=%d, acc=%f" % (step, acc))

                # 如果准确率大于阀值,完成训练
                if acc > MAX_ACC_LINE:
                    break

        # 保存CKPT模型
        saver.save(sess, CKPT_MODEL_PATH, global_step=step)

        # 保存PB模型
        constant_graph = graph_util.convert_variables_to_constants(sess, sess.graph_def, ["final_output"])
        with tf.gfile.FastGFile(PB_MODEL_PATH, mode='wb') as pb:
            pb.write(constant_graph.SerializeToString())



# 通过CKPT还原CNN模型，并测试
def crack_captcha_by_ckpt():
    output = crack_captcha_cnn()

    saver = tf.train.Saver()
    with tf.Session() as sess:
        saver.restore(sess, tf.train.latest_checkpoint(CKPT_MODEL_DIR))

        for _ in range(10):
            text, image = get_name_and_image(TEST_DIR, TEST_CAPTCHA_NUM)
            image = 1 * (image.flatten())   # flatten作用是把image降维到1维, 乘1是把 bool数组 转为 01数组
            predict = tf.argmax(tf.reshape(output, [-1, MAX_CAPTCHA, CHAR_SET_LEN]), 2)
            text_list = sess.run(predict, feed_dict={X: [image], keep_prob: 1.0})
            vec = text_list[0].tolist()
            predict_text = vec2name(vec)
            print("期望值: {}  预测值: {}".format(text, predict_text))


# 通过导入的PB模型进行测试
def crack_captcha_by_pb():
    with tf.Graph().as_default():
        output_graph_def = tf.GraphDef()

        with open(PB_MODEL_PATH, "rb") as pb:
            output_graph_def.ParseFromString(pb.read())
            tf.import_graph_def(output_graph_def, name="") # 此处name是必须的， 保存pb的时候没有指定name, 此处为空串即可

        with tf.Session() as sess:
            sess.run(tf.global_variables_initializer())

            X = sess.graph.get_tensor_by_name("image_input:0")
            keep_prob = sess.graph.get_tensor_by_name("keep_prob:0")
            output = sess.graph.get_tensor_by_name("final_output:0")
            predict = tf.argmax(tf.reshape(output, [-1, MAX_CAPTCHA, CHAR_SET_LEN]), 2)

            for _ in range(10):
                text, image = get_name_and_image(TEST_DIR, TEST_CAPTCHA_NUM)
                image = 1 * (image.flatten())   # flatten作用是把image降维到1维, 乘1是把 bool数组 转为 01数组

                text_list = sess.run(predict, feed_dict={X: [image], keep_prob: 1.0})
                vec = text_list[0].tolist()
                predict_text = vec2name(vec)
                print("期望值: {}  预测值: {}".format(text, predict_text))





if __name__ == '__main__':
    # train_crack_captcha_cnn()   # 训练模型
    crack_captcha_by_ckpt()       # 测试模式
    crack_captcha_by_pb()         # 测试模式

