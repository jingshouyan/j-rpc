<script type="text/html" id="ID_TPL_LEFTMENU">
	<ul class="ore-menu">
	{{each list as menu i}}		
		<li class="ore-menu-item">
			<p class="ore-menu-text" data-state="0" data-lcontrollerl="0" data-id="{{menu.link}}" title="{{menu.title}}"><i class="icon icon-img"><img src="{{menu.icon}}"/></i>{{menu.name | translate}}<i class="icon icon-fold"></i></p>
			<ul class="ore-menu ore-menu-sub">
				{{each menu.children as submenu j}}
					<li class="ore-menu-item" ><p class="ore-menu-text ore-submenu-text" data-lcontrollerl="1" data-id="{{menu.link}}_{{submenu.link}}" title="{{submenu.title}}" data-router="/{{menu.link}}/{{submenu.link}}">{{submenu.title | translate}}</p></li>
				{{/each}}
			</ul>
		</li>		
	{{/each}}
	</ul>		
</script>

<script type="text/html" id="ID_TPL_MENU">
	<ul class="ore-menu">
	{{each list as menu i}}		
		<li class="ore-menu-item">
			<p class="ore-menu-text" id="{{menu.name}}_{{i}}" onclick="controller.loadInterfaceList(this, '{{menu.name}}', '{{menu.name}}_{{i}}'); " data-state="0" data-level="0" data-id="images/icon/add.png" title="{{menu.name + "_" + menu.version}}"><i class="icon icon-img" ><img src="{{menu.icon}}"/></i>{{menu.name | translate}}<i class="icon icon-fold icon-switchstate" ></i></p>
		</li>		
	{{/each}}
	</ul>		
</script>

<script type="text/html" id="ID_TPL_SUBMENU">	
	<ul class="ore-menu ore-menu-sub">
		{{each list as submenu j}}
			<li class="ore-menu-item">
				<p class="ore-menu-text ore-submenu-text" id="{{submenu.name}}_{{i}}" onclick="controller.loadInterfaceDetail(this, '{{submenu.name}}');" data-level="1" data-id="{{submenu.name}}" title="{{submenu.name}}">{{submenu.name | translate}}</p>
			</li>
		{{/each}}
	</ul>			
</script>