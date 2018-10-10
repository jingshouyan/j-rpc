namespace java io.jing.base.thrift

struct TokenBean{
    1:string traceId;
    2:string userId;
    3:string ticket;
    4:i32 clientType;
}
struct ReqBean{
    1:string method;
    2:string param;
    3:bool oneWay;
}

struct RspBean{
    1:i32 code;
    2:string msg;
    3:string result;
}

service MicroService{
	RspBean call(1:TokenBean token,2:ReqBean req);
	oneway void send(1:TokenBean token,2:ReqBean req);
}