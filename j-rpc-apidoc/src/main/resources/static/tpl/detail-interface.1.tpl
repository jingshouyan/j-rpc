<div class="ore-panel vrv-interface-detail">
	<h2>接口详情</h2>	
	<p>所属服务：{{server.name}}</p>
	<p>接口定义：{{inter.output.type}}
	{{ if inter.output.generics.length != 0}}
		&lt;
		{{each inter.output.generics as generic i}}
			{{generic.type}}
			{{if i != inter.output.generics.length-1 }}, &nbsp;{{/if}}
		{{/each}}		
		&gt;
	{{/if}}
	&nbsp;&nbsp;{{inter.name}}&nbsp;(
		{{inter.input.type}}
		
		{{ if inter.input.generics.length != 0}}
			&lt;
			{{each inter.input.generics as generic i}}
				{{generic.type}}
				{{if i != inter.input.generics.length-1 }}, &nbsp;{{/if}}
			{{/each}}		
			&gt;
		{{/if}}
		{{if inter.input.fields.length!=0}}
			(
				{{each inter.input.fields as field i}}
					{{field.type}}
					{{ if field.generics.length != 0}}
						&lt;
						{{each field.generics as generic i}}
							{{generic.type}}
							{{if i != field.generics.length-1 }}, &nbsp;{{/if}}
						{{/each}}		
						&gt;
					{{/if}}
					{{if i != inter.input.fields.length-1 }}, &nbsp;{{/if}}
				{{/each}}
			)
		{{/if}}
		)
	</h2>
	
</div>