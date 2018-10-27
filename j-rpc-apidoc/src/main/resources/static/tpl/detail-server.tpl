<style>
	.ore-text-label{
		display:inline-block;
		min-width: 100px;		
	}
	
	.ore-text-value{
		display:inline-block;
		min-width: 50px;
	}
</style>
<div class="ore-panel vrv-server-detail">	
	<h2>服务详情</h2>
	<p>服务名：{{server.name}}</p>
	<p>版本号：{{server.version}}</p>
	<p>服务端口：{{server.port}}</p>
	<h2>错误码</h2>
	<ul id="111">
		{{each server.codeInfos as codeInfo i}}
			<li><span class="ore-text-value">{{codeInfo.code}}</span> ：<span  class="ore-text-value">{{codeInfo.message}}</span></li>
		{{/each}}
	</ul>
</div>
