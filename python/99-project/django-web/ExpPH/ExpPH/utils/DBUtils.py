# -*- coding: utf8 -*-
'''
数据库工具
Created on 2014年5月14日

@author: Exp
'''


''' 获取数据库连接参数 '''
def getDbParams(DBType = 'mysql'):
    from ExpPH.utils.BaseUtils import isLocalEnvironment
    import ExpPH.conf.Keys as ECK
    import ExpPH.conf.Consts as ECC
    
    # 数据库参数字典
    dbPms = {}
    
    # LOCAL 本地环境数据库
    if isLocalEnvironment():
        if DBType == ECC.DB_TYPE_MYSQL:
            dbPms = {ECK.DB_ENGINE : ECC.DB_ENGINE_MYSQL,     # 数据库引擎
                     ECK.DB_NAME : 'ExpPH',       # 数据库名称
                     ECK.DB_USER : 'root',        # 账户
                     ECK.DB_PSWD : 'root',        # 密码
                     ECK.DB_IP_M : '127.0.0.1',   # 主服务器IP
                     ECK.DB_IP_S : '127.0.0.1',   # 从服务器IP
                     ECK.DB_PORT : '3306',        # 端口
                     }
            
        elif DBType == ECC.DB_TYPE_SQLITE:
            dbPms = {ECK.DB_ENGINE : ECC.DB_ENGINE_SQLITE,   # 数据库引擎
                     ECK.DB_NAME : './static/data/expphDB.sqlite3',  # 数据库名称
                     ECK.DB_USER : '',            # 账户
                     ECK.DB_PSWD : '',            # 密码
                     ECK.DB_IP_M : '',            # 主服务器IP
                     ECK.DB_IP_S : '',            # 从服务器IP
                     ECK.DB_PORT : '',            # 端口
                     }
        
        elif DBType == ECC.DB_TYPE_ORACLE:
            pass
        
        else:
            pass
        
    # SAE 环境，暂时只支持mysql数据库
    else: 
        import sae.const 
        dbPms = {ECK.DB_ENGINE : ECC.DB_ENGINE_MYSQL,    # 数据库引擎
                 ECK.DB_NAME : sae.const.MYSQL_DB,       # 数据库名称
                 ECK.DB_USER : sae.const.MYSQL_USER,     # 账户
                 ECK.DB_PSWD : sae.const.MYSQL_PASS,     # 密码
                 ECK.DB_IP_M : sae.const.MYSQL_HOST,     # 主服务器IP
                 ECK.DB_IP_S : sae.const.MYSQL_HOST_S,   # 从服务器IP
                 ECK.DB_PORT : sae.const.MYSQL_PORT,     # 端口
                 }
        
    return dbPms
# End Fun dbParams()
        
        
'''
    # 本地的Key-valus库
    # KV文件位置为 ./static/conf/kv.conf
    '''
class LocalKVDB:
    
    ''' 初始化 '''
    def __init__(self):
        self.kvFilePath = './static/conf/kv.conf'
    # End Fun __init__()
        
        
    ''' 插入一个KV对，若key已存在则覆盖 '''
    def set(self, key, val):
        kvFile = open(self.kvFilePath, 'r+')
        fileData = kvFile.readlines()
        
        # 覆盖所有同名 KV 对
        for line in fileData[:]:
            if line.startswith("".join([str(key), " = "])):
                fileData.remove(line)
                
        # 使得最近使用过的KV对被置顶
        fileData.insert(0, "".join([str(key), " = ", str(val), '\n']))
        
        kvFile.truncate(0)
        kvFile.seek(0, 0)
        for line in fileData:
            kvFile.write(line)
            
        kvFile.close()
        return None
    # End Fun put()
        
    ''' 根据key获取val '''
    def get(self, key):
        with open(self.kvFilePath, 'r') as kvFile:
            fileData = kvFile.readlines()
        kvFile.close()
        
        val = None
        for line in fileData:
            keyStr = "".join([str(key), " = "])
            if line.startswith(keyStr):
                val = line[len(keyStr):]
                break
        
        return val
    # End Fun put()
        
    ''' 移除key '''
    def rmv(self, key):
        kvFile = open(self.kvFilePath, 'r+')
        fileData = kvFile.readlines()
        
        val = None
        for line in fileData[:]:
            if line.startswith("".join([str(key), " = "])):
                fileData.remove(line)
        
        kvFile.truncate(0)  # 清空文件
        kvFile.seek(0, 0)   # 移动文件指针到文件开头
        for line in fileData:
            kvFile.write(line)
                
        kvFile.close()
        return val
    # End Fun rmv()
        
    ''' 清空所有KV对 '''
    def clr(self):
        kvFile = open(self.kvFilePath, 'r+')
        kvFile.truncate(0)  # 清空文件
        kvFile.seek(0, 0)   # 移动文件指针到文件开头
        kvFile.close()
        return None
    # End Fun clr()
            
# End Class LocalKVDB


'''
# Mysql数据库操作
'''
import MySQLdb
class UseMysqlDB:
    
    ''' 初始化 '''
    def __init__(self):
        pass
    # End Fun __init__() 
    
    ''' 连接数据库连接 '''
    def connect(self, charset, **link_db_params):
        isGetConn = False
        try:
            import ExpPH.conf.Keys as ECK
            self.dbConn = MySQLdb.connect(host = link_db_params[ECK.DB_IP_M], 
                                          port = int(link_db_params[ECK.DB_PORT]), 
                                          user = link_db_params[ECK.DB_USER], 
                                          passwd = link_db_params[ECK.DB_PSWD], 
                                          db = link_db_params[ECK.DB_NAME], 
                                          charset = charset
                                          )
            isGetConn = True
        except MySQLdb.Error, e:
            print "Connect to Mysql Error %d: %s" % (e.args[0], e.args[1])
        
        return isGetConn
    # End Fun connect() 
        
        
    ''' 往数据库插入一条记录 '''
    def insert(self, sql):
        isInsert = False
        try:
            cursor = self.dbConn.cursor()
            cursor.execute(sql)
            self.dbConn.commit()
            cursor.close()
            isInsert = True
        except MySQLdb.Error, e:
            print "Exec SQL Error: %s" % (sql)
            print "Mysql Error %d: %s" % (e.args[0], e.args[1])
            
        return isInsert
    # End Fun insert() 
    
    
    ''' 关闭数据库连接 '''
    def close(self):
        isClose = False
        try:
            if not self.dbConn:
                self.dbConn.close()
                
            isClose = True
        except MySQLdb.Error, e:
            print "Close Mysql Connect Error %d: %s" % (e.args[0], e.args[1])
            
        return isClose
    # End Fun close() 
    
    
    ''' 当类的示例离开作用域时执行 ，避免忘记关闭连接 '''
    def __del__(self):
        self.close()
    # End Fun __del__() 
    
# End Class UseMysqlDB
