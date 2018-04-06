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
from src.main.py.bean.cookie import QQCookie
from src.main.py.bean.album import Album


class AlbumAnalyzer(object):
    '''
    【空间相册】解析器
    '''

    ALBUM_NAME = "AlbumInfo-[相册信息].txt" # 相册信息保存文件名
    request_cnt  = 0    # 累计发起请求次数
    cookie = None       # 已登陆的QQCookie
    QQ = ''             # 被爬取数据的目标QQ
    ALBUM_DIR = ''      # 相册保存目录

    def __init__(self, cookie, QQ):
        '''
        构造函数
        :param cookie: 已登陆的QQCookie
        :param QQ: 被爬取数据的目标QQ
        :return:None
        '''
        self.cookie = QQCookie() if not cookie else cookie
        self.QQ = '0' if not QQ else QQ.strip()
        self.ALBUM_DIR = '%s%s/album/' % (cfg.DATA_DIR, self.QQ)


    def execute(self):
        '''
        执行空间相册解析, 并下载所有相册及其内的照片
        :return:None
        '''
        try:

            # 清除上次下载的数据
            if os.path.exists(self.ALBUM_DIR):
                shutil.rmtree(self.ALBUM_DIR)
            os.makedirs(self.ALBUM_DIR)

            # 下载相册
            albums = self.get_albums()


            print('任务完成: QQ [%s] 的空间相册已保存到 [%s]' % (self.QQ, self.ALBUM_DIR))
        except:
            print('任务失败: 下载 QQ [%s] 的空间相册时发生异常' % self.QQ)
            traceback.print_exc()


    def get_albums(self):
        '''
        提取所有相册及其内的照片信息
        :return: 相册列表（含照片信息）
        '''
        albums = self.get_album_list()
        for album in albums:
            self.open(album)
        return albums


    def get_album_list(self):
        '''
        获取相册列表
        :return: 相册列表(仅相册信息, 不含内部照片信息)
        '''
        print('正在提取QQ [%s] 的相册列表...' % self.QQ)
        response = requests.get(url=cfg.ALBUM_LIST_URL,
                                headers=xhr.get_headers(self.cookie.to_nv()),
                                params=self._get_album_parmas())

        try:
            root = json.load(xhr.to_json(response.text))
            data = root['data']
            album_list = data['albumListModeSort']

            albums = []
            for album in album_list :
                name = album['name']
                question = album['question']

                if not question :
                    total = album['total']
                    id = album['id']
                    url = cfg.ALBUM_URL(self.QQ, id)
                    albums.append(Album(id, name, url, total))
                    print('获得相册 [%s] (照片x%d), 地址: %s' % (name, total, url))

                else:
                    print('相册 [%s] 被加密, 无法读取' % name)
        except:
            print('提取QQ [%s] 的相册列表异常' % name)
            traceback.print_exc()

        return albums


# 			JSONObject json = JSONObject.fromObject(XHRUtils.toJson(response));
# 			JSONObject data = JsonUtils.getObject(json, XHRAtrbt.data);
# 			JSONArray albumList = JsonUtils.getArray(data, XHRAtrbt.albumListModeSort);
# 			for(int i = 0; i < albumList.size(); i++) {
# 				JSONObject album = albumList.getJSONObject(i);
# 				String name = JsonUtils.getStr(album, XHRAtrbt.name);
# 				String question = JsonUtils.getStr(album, XHRAtrbt.question);
#
# 				if(StrUtils.isEmpty(question)) {
# 					int total = JsonUtils.getInt(album, XHRAtrbt.total, 0);
# 					String id = JsonUtils.getStr(album, XHRAtrbt.id);
# 					String url = URL.ALBUM_URL(QQ, id);
#
# 					albums.add(new Album(id, name, url, total));
# 					UIUtils.log("获得相册 [", name, "] (照片x", total, "), 地址: ", url);
#
# 				} else {
# 					UIUtils.log("相册 [", name, "] 被加密, 无法读取");
# 				}
# 			}
# 		} catch(Exception e) {
# 			UIUtils.log(e, "提取QQ [", QQ, "] 的相册列表异常");
# 		}
#
# 		UIUtils.log("提取QQ [", QQ, "] 的相册列表完成: 共 [", albums.size(), "] 个相册");
# 		return albums;
# 	}


    def _get_album_parmas(self):
        '''
        获取相册请求参数
        :return:
        '''
        params = self._get_parmas()
        params['handset'] = '4'
        params['filter'] = '1'
        params['needUserInfo'] = '1'
        params['pageNumModeSort'] = '40'
        params['pageNumModeClass'] = '15'
        return params


    def _get_parmas(self):
        '''
        获取相册/照片请求参数
        :return:
        '''
        params = {
            'g_tk' : self.cookie.gtk,
            'callback' : 'shine%d_Callback' % self.request_cnt,
            'callbackFun' : 'shine%d' % self.request_cnt,
            '_' : '%d' % int(time.time()),
            'uin' : self.cookie.uin,
            'hostUin' : self.QQ,
            'inCharset' : cfg.DEFAULT_CHARSET,
            'outCharset' : cfg.DEFAULT_CHARSET,
            'source' : 'qzone',
            'plat' : 'qzone',
            'format' : 'jsonp',
            'notice' : '0',
            'appid' : '4',
            'idcNum' : '4'
        }
        self.request_cnt += 1
        return params






    def open(self, album):
        '''
        打开相册, 提取其中的所有照片信息
        :param album: 相册信息
        :return: None
        '''


    def get_page_photos(self, album, page):
        '''
        获取相册的分页照片信息
        :param album: 相册信息
        :param page: 页数
        :return: 分页照片信息
        '''


    #
	# /**
	#  * 下载所有相册及其内的照片
	#  * @param albums 相册集（含照片信息）
	#  */
	# protected void download(List<Album> albums) {
	# 	if(ListUtils.isEmpty(albums)) {
	# 		return;
	# 	}
    #
	# 	UIUtils.log("提取QQ [", QQ, "] 的相册及照片完成, 开始下载...");
	# 	for(Album album : albums) {
	# 		FileUtils.createDir(ALBUM_DIR.concat(album.NAME()));
	# 		StringBuilder albumInfos = new StringBuilder(album.toString());
    #
	# 		UIUtils.log("正在下载相册 [", album.NAME(), "] 的照片...");
	# 		int cnt = 0;
	# 		for(Photo photo : album.getPhotos()) {
	# 			boolean isOk = _download(album, photo);
	# 			cnt += (isOk ? 1 : 0);
	# 			albumInfos.append(photo.toString(isOk));
    #
	# 			UIUtils.log(" -> 下载照片进度(", (isOk ? "成功" : "失败"), "): ", cnt, "/", album.PIC_NUM());
	# 			ThreadUtils.tSleep(Config.SLEEP_TIME);
	# 		}
	# 		UIUtils.log(" -> 相册 [", album.NAME(), "] 下载完成, 成功率: ", cnt, "/", album.PIC_NUM());
    #
	# 		// 保存下载信息
	# 		String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", ALBUM_NAME);
	# 		FileUtils.write(savePath, albumInfos.toString(), Config.CHARSET, false);
	# 	}
	# }
    #
	# /**
	#  * 下载单张照片
	#  * @param album 照片所属的相册信息
	#  * @param photo 照片信息
	#  * @return 是否下载成功
	#  */
	# protected boolean _download(Album album, Photo photo) {
	# 	Map<String, String> header = XHRUtils.getHeader(Browser.COOKIE());
	# 	header.put(HttpUtils.HEAD.KEY.HOST, XHRUtils.toHost(photo.URL()));
	# 	header.put(HttpUtils.HEAD.KEY.REFERER, album.URL());
    #
	# 	boolean isOk = false;
	# 	String savePath = StrUtils.concat(ALBUM_DIR, album.NAME(), "/", photo.getPicName());
	# 	for(int retry = 0; !isOk && retry < Config.RETRY; retry++) {
	# 		isOk = HttpURLUtils.downloadByGet(savePath, photo.URL(), header, null);
	# 		if(isOk == false) {
	# 			FileUtils.delete(savePath);
	# 		}
	# 	}
	# 	return isOk;
	# }