<style>
	.ore-text-label{
		display:inline-block;
		min-width: 80px;		
	}
	
	.ore-text-value{
		display:inline-block;
		min-width: 50px;
	}
</style>
<div class="ore-panel vrv-interface-detail">
	<h2>接口详情</h2>	
	<p><span class="ore-text-label">所属服务：</span><span class="ore-text-value">{{server.name}}</span></p>
	<p><span class="ore-text-label">接口定义：</span><span class="ore-text-value">{{inter.output.rootType}}&nbsp;&nbsp;{{inter.name}}&nbsp;({{inter.input.rootType}})
	
	<h3><span class="ore-text-label">入参：</span><span class="ore-text-value">{{inter.input.rootType}}</span></h3>
	<ul class="clzz-list">
	{{each inter.input.types as type i}}
		<li style="border:1px solid #AAA; margin-bottom: 10px;">
		<p>{{type.type}}</p>
		{{each type.fields as field j}}
		<p>&nbsp;&nbsp;&nbsp;&nbsp;{{field.type}}&nbsp;&nbsp;{{field.name}}</p>
		{{/each}}
		</li>
	{{/each}}
	</ul>
	
	<h3><span class="ore-text-label">返回值：</span><span class="ore-text-value">{{inter.output.rootType}}</span></h3>
	<ul class="clzz-list">
	{{each inter.output.types as type i}}
		<li style="border:1px solid #AAA; margin-bottom: 10px;">
		<p>{{type.type}}</p>
		{{each type.fields as field j}}
		<p>&nbsp;&nbsp;&nbsp;&nbsp;{{field.type}}&nbsp;&nbsp;{{field.name}}</p>
		{{/each}}
		</li>
	{{/each}}
	</ul>
</div>