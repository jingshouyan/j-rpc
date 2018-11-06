namespace java com.github.jingshouyan.jrpc.base.thrift

struct TokenBean{
    1:string userId;
    2:string ticket;
    3:map<string,string> headers;
}
struct ReqBean{
    1:string method;
    2:string param;
}

struct RspBean{
    1:i32 code;
    2:string message;
    3:string result;
}

service Jrpc{
	RspBean call(1:TokenBean token,2:ReqBean req);
	oneway void send(1:TokenBean token,2:ReqBean req);
}