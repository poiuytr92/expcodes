# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'

import os
import shutil
import requests
import time
import json
import traceback
import src.main.py.config as cfg
import src.main.py.utils.xhr as xhr
import src.main.py.utils.pic as pic
from src.main.py.bean.cookie import QQCookie


class MoodAnalyzer(object):
    '''
    【空间说说】解析器
    '''

    MOOD_INFO_NAME = 'MoodInfo-[说说信息].txt'  # 说说分页信息保存文件名
    cookie = None           # 已登陆的QQCookie
    QQ = ''                 # 被爬取数据的目标QQ
    MOOD_DIR = ''           # 说说保存目录
    PAGE_DIR_PREFIX = ''    # 说说每页图文信息的保存路径前缀
    PHOTO_DIR = ''          # 说说所有照片的保存目录


    def __init__(self, cookie, QQ):
        '''
        构造函数
        :param cookie: 已登陆的QQCookie
        :param QQ: 被爬取数据的目标QQ
        :return:None
        '''
        self.cookie = QQCookie() if not cookie else cookie
        self.QQ = '0' if not QQ else QQ.strip()
        self.MOOD_DIR = '%s%s/mood/' % (cfg.DATA_DIR, self.QQ)
        self.PAGE_DIR_PREFIX = '%scontent/page-' % self.MOOD_DIR
        self.PHOTO_DIR = '%sphotos/' % self.MOOD_DIR


    def execute(self):
        '''
        执行空间说说解析, 并下载所有说说及相关照片
        :return:
        '''
        try:

            # 清除上次下载的数据
            if os.path.exists(self.MOOD_DIR):
                shutil.rmtree(self.MOOD_DIR)
            os.makedirs(self.MOOD_DIR)

            # 下载说说及照片
            moods = self.get_moods()


            print('任务完成: QQ [%s] 的空间说说已保存到 [%s]' % (self.QQ, self.MOOD_DIR))

        except:
            print('任务失败: 下载 QQ [%s] 的空间说说时发生异常' % self.QQ)
            traceback.print_exc()


    def get_moods(self):
        '''
        提取所有说说及相关的照片信息
        :return: 说说列表（含相关照片信息）
        '''
        print('正在提取QQ [%s] 的说说动态...' % self.QQ)
        moods = []
        PAGE_NUM = self.get_page_num()
        for page in range(PAGE_NUM) :
            page += 1
            print(' -> 正在提取第 [%d/%d] 页的说说信息...' % (page, PAGE_NUM))

            # TODO

            print(' -> 第 [%d/%d] 页说说提取完成, 累计说说数量: %d' % (page, PAGE_NUM, len(moods)))
            time.sleep(cfg.SLEEP_TIME)

        return moods


    def get_page_num(self):
        '''
        获取说说总页数
        :return: 说说总页数
        '''
        print('正在提取QQ [%s] 的说说页数...' % self.QQ)
        total = 0
        try:
            root = json.loads(self.get_page_moods_json(1))
            total = root.get('total', 0)

        except:
            print('提取QQ [%s] 的说说页数失败' % self.QQ)
            traceback.print_exc()

        return total


    def get_page_moods(self, page):
        '''
        获取分页的说说内容
        :param page: 页码
        :return: 分页说说列表（含相关照片信息）
        '''
        moods = []
        try:
            root = json.loads(self.get_page_moods_json(page))
            msg_list = root['msglist']
            for msg in msg_list :
                content = msg.get('content', '')
                create_time = msg.get('created_time', 0) * 1000


        except:
            print('提取第 [%d] 页的说说信息异常' % page)
            traceback.print_exc()

        return moods

# 			JSONObject json = JSONObject.fromObject(response);
# 			JSONArray msglist = JsonUtils.getArray(json, XHRAtrbt.msglist);
# 			for(int i = 0; i < msglist.size(); i++) {
# 				JSONObject msg = msglist.getJSONObject(i);
# 				String content = JsonUtils.getStr(msg, XHRAtrbt.content);
# 				long createTime = JsonUtils.getLong(msg, XHRAtrbt.created_time, 0) * 1000;
#
# 				Mood mood = new Mood(page, content, createTime);
# 				JSONArray pics = JsonUtils.getArray(msg, XHRAtrbt.pic);
# 				for(int j = 0; j < pics.size(); j++) {
# 					JSONObject pic = pics.getJSONObject(j);
# 					String url = JsonUtils.getStr(pic, XHRAtrbt.url3);
# 					url = PicUtils.convert(url);
# 					mood.addPicURL(url);
# 				}
# 				moods.add(mood);
# 	}


    def get_page_moods_json(self, page):
        '''
        获取分页的说说Json
        :param page: 页码
        :return: 分页的说说Json
        '''
        headers = xhr.get_headers(self.cookie.to_nv())
        headers['Referer'] = cfg.MOOD_REFERER
        params = {
            'g_tk' : self.cookie.gtk,
            'qzonetoken' : self.cookie.qzone_token,
            'uin' : self.QQ,
            'hostUin' : self.QQ,
            'pos' : '%d' % ((page - 1) * cfg.BATCH_LIMT),
            'num' : '%d' % cfg.BATCH_LIMT,
            'cgi_host' : cfg.MOOD_DOMAIN,
            'inCharset' : cfg.DEFAULT_CHARSET,
            'outCharset' : cfg.DEFAULT_CHARSET,
            'notice' : '0',
            'sort' : '0',
            'code_version' : '1',
            'format' : 'jsonp',
            'need_private_comment' : '1',
        }
        response = requests.get(cfg.MOOD_URL, headers=headers, params=params)
        return xhr.to_json(response.text)


#
# 	/**
# 	 * 下载所有说说及相关的照片
# 	 * @param moods 说说集（含照片信息）
# 	 */
# 	private void download(List<Mood> moods) {
# 		if(ListUtils.isEmpty(moods)) {
# 			return;
# 		}
#
# 		UIUtils.log("提取QQ [", QQ, "] 的说说及照片完成, 开始下载...");
# 		int idx = 1;
# 		for(Mood mood : moods) {
# 			UIUtils.log("正在下载第 [", idx++, "/", moods.size(), "] 条说说: ", mood.CONTENT());
# 			int cnt = _download(mood);
# 			boolean isOk = (cnt == mood.PIC_NUM());
# 			UIUtils.log(" -> 说说照片下载完成, 成功率: ", cnt, "/", mood.PIC_NUM());
#
# 			// 保存下载信息
# 			String savePath = StrUtils.concat(PAGE_DIR_PREFIX, mood.PAGE(), "/", MOOD_INFO_NAME);
# 			FileUtils.write(savePath, mood.toString(isOk), Config.CHARSET, true);
# 		}
# 	}
#
# 	/**
# 	 * 下载单条说说及相关的照片
# 	 * @param mood 说说信息
# 	 * @return 成功下载的照片数
# 	 */
# 	private int _download(Mood mood) {
# 		Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
# 		header.put(HttpUtils.HEAD.KEY.REFERER, URL.MOOD_REFERER);
#
# 		int idx = 0, cnt = 0;
# 		for(String picURL : mood.getPicURLs()) {
# 			String picName = PicUtils.getPicName(String.valueOf(idx++), mood.CONTENT());
# 			boolean isOk = _download(header, mood.PAGE(), picName, picURL);
# 			cnt += (isOk ? 1 : 0);
#
# 			UIUtils.log(" -> 下载照片进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", mood.PIC_NUM());
# 		}
# 		return cnt;
# 	}
#
# 	/**
# 	 * 下载单张图片到说说的分页目录，并复制到图片合集目录
# 	 * @param header
# 	 * @param pageIdx 页码索引
# 	 * @param picName
# 	 * @param picURL
# 	 * @return
# 	 */
# 	private boolean _download(Map<String, String> header,
# 			String pageIdx, String picName, String picURL) {
# 		header.put(HttpUtils.HEAD.KEY.HOST, XHRUtils.toHost(picURL));
#
# 		boolean isOk = false;
# 		String savePath = StrUtils.concat(PAGE_DIR_PREFIX, pageIdx, "/", picName);
# 		for(int retry = 0; !isOk && retry < Config.RETRY; retry++) {
# 			isOk = HttpURLUtils.downloadByGet(savePath, picURL, header, null,
# 					Config.TIMEOUT, Config.TIMEOUT, Config.CHARSET);
#
# 			if(isOk == false) {
# 				FileUtils.delete(savePath);
# 				ThreadUtils.tSleep(Config.SLEEP_TIME);
#
# 			} else {
# 				FileUtils.copyFile(savePath, PHOTO_DIR.concat(picName));
# 			}
# 		}
# 		return isOk;
# 	}
#