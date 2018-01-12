<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>管理员管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
<#include  "/WEB-INF/ftl/common/header.ftl" >
<script type="text/javascript" src="${domainStatic}/resources/js/sso_admin/sso_admin.js?version=${staticVersion}"></script>
<script type="text/javascript">
    $(function(){
       toolSearch();
    });    
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'north',border:false" class="toolbar-region">
    <@p.toolbar id="toolbar" listData=[
                         {"id":"btn_search","title":"查询","iconCls":"icon-search", "type":0,"action":"sso_admin.btnSearch();","disabled":"false"},
                         {"id":"btn_clear","title":"清空","iconCls":"icon-remove","action":"$('#searchForm').form('clear');", "type":0,"disabled":"false"},
                         {"id":"btn-add","title":"新增","iconCls":"icon-add","action":"sso_admin.add();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"修改","iconCls":"icon-edit","action":"sso_admin.edit();", "type":0,"disabled":"false"},
                         {"id":"btn-edit","title":"删除","iconCls":"icon-edit","action":"sso_admin.del();", "type":0,"disabled":"false"}
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
                          <td>用户名/真实姓名：</td><td><input class="easyui-validatebox" style="width:100px" name="adminName" id="adminName"/></td>
                          <td>角色：</td>
                          <td>
                          	<select id="roleIdP"  name="roleIdP" style="width:100px;" class="easyui-combobox" data-options="editable:false">
                          		<option value="">请选择角色</option>
								<#if roleList??>
									<#list roleList as role>
										<option value="${role.id}">${role.roleName}</option>
									</#list>
								</#if>
							</select>
                          </td>
				       </tr>
				</table>
			</form>
			</div>
	   </div>
 	<div data-options="region:'center'">
 		<@p.datagrid id="sso_admin_list" name="" title="管理员列表" loadUrl="/sso_admin/list.json?sort=id&order=desc" saveUrl="" defaultColumn="" 
 			isHasToolBar="false" divToolbar="" width="" onClickRowEdit="true" singleSelect="false" pageSize='50'  checkOnSelect="true" selectOnCheck="true" 
			pagination="true" rownumbers="true" reduceHeight=54
 			columnsJsonList="[
 				{field:'id',title:'id',width:70,hidden:true
 				},
 				{field:'ck',checkbox:true
 				},
 				{field:'loginName',title:'用户名',width:90
 				},
 				{field:'sureName',title:'真实姓名',width:120
 				},
 				{field:'phone',title:'手机号码',width:120
 				},
 				{field:'email',title:'邮箱',width:120
 				},
 				{field:'adminType',title:'管理员类型',width:160,
 					formatter: function(value,row,index){
	                			if (value == 1) {
	                				return '系统管理员 ';
	                			}else if (value == 2) {
	                				return '超级管理员';
	                			}
	    						return '普通管理员';
	    					}
 				},
 				{field:'roleId',title:'管理员角色Id',width:160,hidden:true
 				},
 				{field:'roleName',title:'管理员角色',width:160
 				},
 				{field:'state',title:'状态',width:150,
                	formatter: function(value,row,index){
	                			if (value == '1') {
	                				return '正常 ';
	                			}else if (value == '2') {
	                				return '已锁定';
	                			}
	    						return '';
	    					}
 				},
 				{field:'description',title:'描述',width:150
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
		title: '添加管理员',
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
				<label>用户名:</label>
				<input id="loginName" validType="loginNameLength" class="easyui-validatebox ipt" name="loginName" style="width:200px;" required="true"/>
			</div>	
			<div class="fitem">
				<label>真实姓名:</label>
				<input id="sureName" validType="loginNameLength" class="easyui-validatebox ipt" name="sureName" style="width:200px;" required="true"/>
			</div>
			<div class="fitem">
				<label>密码:</label>
				<input autocomplete="new-password" id="password" type="password" class="easyui-validatebox ipt" name="password" style="width:200px;"  required="true" validType="adminPwd" />
			</div>
			<div class="fitem">
				<label>管理员类型:</label>
				<select id="adminType"  name="adminType" style="width:200px;" class="easyui-combobox ipt" required="true" data-options="editable:false">
					<#if Session.adminType==1>
						<option value="0">普通管理员</option>
						<option value="2">超级管理员</option>
					</#if>
					<#if Session.adminType==2>
						<option value="0">普通管理员</option>
					</#if>
				</select>
			</div>
			<div class="fitem">
				<label>管理员角色:</label>
				<select id="roleId"  name="roleId" style="width:200px;" class="easyui-combobox ipt" required="true" data-options="editable:false">
				</select>
			</div>
			<div class="fitem">
				<label>状态:</label>
				<select id="state"  name="state" style="width:200px;" class="easyui-combobox ipt" required="true" data-options="editable:false">
					<option value="1">正常</option>
					<option value="2">锁定</option>
				</select>
			</div>
			<div class="fitem">
				<label>手机号码:</label>
				<input id="phone" class="easyui-validatebox ipt" validType="phoneNum" name="phone" style="width:200px;"  />
			</div>
			<div class="fitem">
				<label>邮箱:</label>
				<input id="email" class="easyui-validatebox ipt" validType='email'  name="email" style="width:200px;"  />
			</div>
			<div class="fitem">
				<label>描述:</label>
				<input id="description" class="easyui-validatebox ipt" name="description" style="width:200px;"  />
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