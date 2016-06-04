include "vo.thrift"

namespace java service.demo 

service Hello { 
  string helloString(1:string para) 
  i32 helloInt(1:i32 para) 
  bool helloBoolean(1:bool para) 
  void helloVoid() 
  string helloNull() 
  vo.Person getPerson(1:i32 id, 2:string name)
  vo.Course getCourse(1:i32 id)
}