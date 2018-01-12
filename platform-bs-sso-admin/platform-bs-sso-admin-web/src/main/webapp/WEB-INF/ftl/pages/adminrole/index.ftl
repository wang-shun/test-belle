<html xmlns="http://www.w3.org/1999/xhtml">
<#include  "/WEB-INF/ftl/common/header.ftl" >
<head>
<title>角色管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
    <script type="text/javascript" src="${domainStatic}/resources/js/admin_role/admin_role_org_tree.js?version=${staticVersion}" ></script>


<script type="text/javascript" src="${domainStatic}/resources/js/admin_role/admin_role.js?version=${staticVersion}"></script>
<script type="text/javascript">
    $(function(){
       toolSearch();
    })
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'north',border:false" class="toolbar-region">
    <@p.toolbar id="toolbar" listData=[
                         {"id":"btn_search","title":"查询","iconCls":"icon-search", "type":0,"action":"admin_role.btnSearch();","disabled":"false"},
                         {"id":"btn_clear","title":"清空","iconCls":"icon-remove","action":"$('#searchForm').form('clear');", "type":0,"disabled":"false"},
                         {"id":"btn-add","title":"新增","iconCls":"icon-add","action":"admin_role.add();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"修改","iconCls":"icon-edit","action":"admin_role.edit();", "type":0,"disabled":"false"},
                         {"id":"btn-edit","title":"删除","iconCls":"icon-edit","action":"admin_role.del();", "type":0,"disabled":"false"}
                       ]
       />
</div>
<div data-options="region:'center',border:false">
 	<div id="subLayout" class="easyui-layout" data-options="fit:true,border:false">
        <div data-options="region:'north',border:false" >
            <div class="search-div">
			<form name="searchForm" id="searchForm" method="post" style="margin:0px">
				<table class="search-tb">
                 <tr>
                  <td>角色名称：</td><td><input class="easyui-validatebox" style="width:100px" name="roleNameP" id="roleNameP"/></td>
                  <td>角色描述：</td><td><input class="easyui-validatebox" style="width:100px" name="descriptionP" id="descriptionP"/></td>
		         </tr>
				</table>
			</form>
			</div>
	   </div>
 	<div data-options="region:'center'">
 		<@p.datagrid id="admin_role_list" name="" title="管理员角色列表" loadUrl="/admin_role/list.json?sort=id&order=desc" saveUrl="" defaultColumn="" 
 			isHasToolBar="false" divToolbar="" width="" onClickRowEdit="true" singleSelect="false" pageSize='50'  checkOnSelect="true" selectOnCheck="true" 
			pagination="true" rownumbers="true" reduceHeight=54
 			columnsJsonList="[
 				{field:'id',title:'id',width:70,hidden:true
 				},
 				{field:'ck',checkbox:true
 				},
 				{field:'roleName',title:'角色名称',width:90
 				},
 				{field:'description',title:'描述',width:200
 				},
 				{field:'orgUnitList',title:'管理的机构',width:200,
 					formatter: function(value,row,index){
	                			var text='';
						    	$(value).each(function (index, obj) {
									text += obj.fullName;
									text += ',';
						    	});
						    	text = text.substring(0,text.length-1);
	    						return text;
	    					}
 				},
 				{field:'createUser',title:'创建人',width:80
 				},
 				{field:'updateTime',title:'更新时间',width:150,
                	formatter: function(value,row,index){
	                			if (value == null || value == '') {
	                				return null;
	                			}
	    						return getFormatDateByLong(value, 'yyyy-MM-dd hh:mm:ss');
	    					}
 				}
 			]"
 		/>
 	  </div>	
    </div>	
</div>

<div id="dlg" class="easyui-dialog" style="width:500px;height:450px;" closed="true" buttons="#dlg-buttons"
	data-options="
		title: '添加角色',
		onClose: function() {
			$('#fm').form('disableValidation');
		}
	"
>
		<form id="fm" method="post" novalidate>
			<div class="fitem">
			<input id="id"  name="id" type="hidden"/>
			</div>	
			
			<div class="fitem">
				<label>角色名称:</label>
				<input id="roleName" class="easyui-validatebox ipt" name="roleName" style="width:200px;" required="true"/>
			</div>	
			<div class="fitem">
				<label>描述:</label>
				<input id="description" class="easyui-validatebox ipt" name="description" style="width:200px;" required="true"/>
			</div>
			<div class="fitem">
				<label>管理的机构:</label>
				<!-- 机构树 -->
		        <div class="easyui-tree" data-options="region:'west',split:true, border:true" title="组织树（点此处展开）" style="width:260px; height:100%; border:0px solid #000000;">
				<ul id="org-unit-tree" style="width:260px;height:200px;" >
				</ul>
			</div>
			</div>
			
			
		</form>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton"  id="save">保存</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"  onclick="javascript:$('#dlg').dialog('close')">取消</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
</div>


</body>
</html>