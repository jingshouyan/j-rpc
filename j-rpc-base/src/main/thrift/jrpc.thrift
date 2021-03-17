namespace java com.github.jingshouyan.jrpc.base.thrift

struct TokenBean{
    1:optional string userId;
    2:optional string ticket;
    3:optional map<string,string> headers;
}

struct ThriftHeaders {
    1:optional map<string,string> header;
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