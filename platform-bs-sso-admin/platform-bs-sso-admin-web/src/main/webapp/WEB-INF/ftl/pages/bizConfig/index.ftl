<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>系统业务配置管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
<#include  "/WEB-INF/ftl/common/header.ftl" >
<script type="text/javascript" src="${domainStatic}/resources/js/biz_config/biz_config.js?version=${staticVersion}"></script>
<script type="text/javascript">
    $(function(){
       toolSearch();
    })
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'north',border:false" class="toolbar-region">
    <@p.toolbar id="toolbar" listData=[
                         {"id":"btn_search","title":"查询","iconCls":"icon-search", "type":0,"action":"biz_config.btnSearch();","disabled":"false"},
                         {"id":"btn_clear","title":"清空","iconCls":"icon-remove","action":"$('#searchForm').form('clear');", "type":0,"disabled":"false"},
                         {"id":"btn-add","title":"新增","iconCls":"icon-add","action":"biz_config.add();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"修改","iconCls":"icon-edit","action":"biz_config.edit();", "type":0,"disabled":"false"},
                         {"id":"btn-edit","title":"删除","iconCls":"icon-edit","action":"biz_config.del();", "type":0,"disabled":"false"}
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
                          <td>业务代码/业务名称：</td>
                          <td><input class="easyui-validatebox" style="width:100px" name="queryCondition" id="queryCondition"/></td>
                          
                          <td></td>
                          <td><input class="easyui-validatebox" style="width:100px; display:none;" name="xx" id="xx"/></td>
                          
				       </tr>
				</table>
			</form>
			</div>
	   </div>
 	<div data-options="region:'center'">
 		<@p.datagrid id="biz_config_list" name="" title="业务配置列表" loadUrl="/biz_config/list.json?sort=id&order=desc" saveUrl="" defaultColumn="" 
 			isHasToolBar="false" divToolbar="" width="" onClickRowEdit="true" singleSelect="false" pageSize='50'  checkOnSelect="true" selectOnCheck="true" 
			pagination="true" rownumbers="true" reduceHeight=54
 			columnsJsonList="[
 				{field:'id',title:'id',width:70,hidden:true
 				},
 				{field:'ck',checkbox:true
 				},
 				{field:'bizCode',title:'业务系统代码',width:90
 				},
 				{field:'bizName',title:'业务系统名称',width:120
 				},
 				{field:'email',title:'邮件',width:160
 				},
 				{field:'verifyUserPwdUrl',title:'验证账号URL',width:150
 				},
 				{field:'delUserUrl',title:'删除账号URL',width:150
 				},
 				{field:'loginUrl',title:'登录账号URL',width:150
 				},
 				{field:'syncUserInfoUrl',title:'同步数据URL',width:150
 				},
 				{field:'description',title:'系统描述',width:100
 				},
 				{field:'bizSecret',title:'通信秘钥',width:200
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

<div id="dlg" class="easyui-dialog" style="width:500px;height:350px;" closed="true" buttons="#dlg-buttons"
	data-options="
		title: '添加业务配置',
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
				<label>业务系统代码:</label>
				<input id="bizCode" class="easyui-validatebox ipt" name="bizCode" style="width:330px;" required="true"/>
			</div>	
			<div class="fitem">
				<label>业务系统名称:</label>
				<input id="bizName" class="easyui-validatebox ipt" name="bizName" style="width:330px;" required="true"/>
			</div>
			<div class="fitem">
				<label>邮件:</label>
				<input id="email" class="easyui-validatebox ipt" validType='email' name="email" style="width:330px;"  />
			</div>
			<div class="fitem">
				<label>验证账号URL:</label>
				<input id="verifyUserPwdUrl" class="easyui-validatebox ipt" name="verifyUserPwdUrl" style="width:330px;"  required="true"/>
			</div>
			<div class="fitem">
				<label>删除账号URL:</label>
				<input id="delUserUrl" class="easyui-validatebox ipt" name="delUserUrl" style="width:330px;"  />
			</div>
			<div class="fitem">
				<label>登录账号URL:</label>
				<input id="loginUrl" class="easyui-validatebox ipt" name="loginUrl" style="width:330px;"  />
			</div>
			<div class="fitem">
				<label>同步数据URL:</label>
				<input id="syncUserInfoUrl" class="easyui-validatebox ipt" name="syncUserInfoUrl" style="width:330px;"  />
			</div>
			<div class="fitem">
				<label>系统描述:</label>
				<input id="description" class="easyui-validatebox ipt" name="description" style="width:330px;"  />
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