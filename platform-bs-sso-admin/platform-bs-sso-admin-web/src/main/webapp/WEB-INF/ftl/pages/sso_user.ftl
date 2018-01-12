<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>一账通管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
<#include  "/WEB-INF/ftl/common/header.ftl" >
<script type="text/javascript" src="${domainStatic}/resources/js/sso_user/sso_user_org_tree.js?version=${staticVersion}'/>"></script>
<script type="text/javascript" src="${domainStatic}/resources/js/sso_user/sso_user.js?version=${staticVersion}'/>"></script>
<script type="text/javascript">
    $(function(){
       toolSearch();
    });  

	function copyToClipboard(element) {
      $(element).html("账号："+$("[name=loginName]").val()+" 密码："+$("[name=password]").val());
	  var $temp = $("<input>");
	  $("body").append($temp);
	  $temp.val($(element).text()).select();
	  document.execCommand("copy");
	  $temp.remove();
	}       
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'north',border:false" class="toolbar-region">
	<#if Session.adminType?exists>
	 	<#if Session.adminType== 1>
	 		<@p.toolbar id="toolbar" listData=[
                         {"id":"btn_search","title":"查询","iconCls":"icon-search", "type":0,"action":"sso_user.btnSearch();","disabled":"false"},
                         {"id":"btn_clear","title":"清空","iconCls":"icon-remove","action":"$('#searchForm').form('clear');", "type":0,"disabled":"false"},
                         {"id":"btn-add","title":"新增","iconCls":"icon-add","action":"sso_user.add();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"修改","iconCls":"icon-edit","action":"sso_user.edit();", "type":0,"disabled":"false"},
                         {"id":"btn-edit","title":"删除","iconCls":"icon-edit","action":"sso_user.del();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"绑定业务","iconCls":"icon-edit","action":"sso_user.bind();", "type":0,"disabled":"false"},
                         {"id":"btn_lock","title":"锁定","iconCls":"icon-edit","action":"sso_user.lock();", "type":0,"disabled":"false"},
                         {"id":"btn_unlock","title":"解锁","iconCls":"icon-edit","action":"sso_user.unlock();", "type":0,"disabled":"false"},
                         {"id":"btn_syncSingle","title":"同步","iconCls":"icon-edit","action":"sso_user.syncSingle();", "type":0,"disabled":"false"},
                         {"id":"btn_syncAll","title":"全量同步","iconCls":"icon-edit","action":"sso_user.syncAll();", "type":0,"disabled":"false"},
                         {"id":"btn_exportList","title":"导出","iconCls":"icon-edit","action":"sso_user.exportExcel();", "type":0,"disabled":"false"},
                         {"id":"btn_batchResetPwd","title":"批量重置密码","iconCls":"icon-edit","action":"sso_user.importClick();", "type":0,"disabled":"false"}
                         <!--{"id":"btn_edit","title":"重置密码","iconCls":"icon-edit","action":"sso_user.resetPassword();", "type":0,"disabled":"false"}-->
                         
                       ]
       		/>
	 	<#else>
	 		<@p.toolbar id="toolbar" listData=[
                         {"id":"btn_search","title":"查询","iconCls":"icon-search", "type":0,"action":"sso_user.btnSearch();","disabled":"false"},
                         {"id":"btn_clear","title":"清空","iconCls":"icon-remove","action":"$('#searchForm').form('clear');", "type":0,"disabled":"false"},
                         {"id":"btn-add","title":"新增","iconCls":"icon-add","action":"sso_user.add();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"修改","iconCls":"icon-edit","action":"sso_user.edit();", "type":0,"disabled":"false"},
                         {"id":"btn-edit","title":"删除","iconCls":"icon-edit","action":"sso_user.del();", "type":0,"disabled":"false"},
                         {"id":"btn_edit","title":"绑定业务","iconCls":"icon-edit","action":"sso_user.bind();", "type":0,"disabled":"false"},
                         {"id":"btn_lock","title":"锁定","iconCls":"icon-edit","action":"sso_user.lock();", "type":0,"disabled":"false"},
                         {"id":"btn_unlock","title":"解锁","iconCls":"icon-edit","action":"sso_user.unlock();", "type":0,"disabled":"false"},
                         {"id":"btn_syncSingle","title":"同步","iconCls":"icon-edit","action":"sso_user.syncSingle();", "type":0,"disabled":"false"},
                         {"id":"btn_exportList","title":"导出","iconCls":"icon-edit","action":"sso_user.exportExcel();", "type":0,"disabled":"false"}
                         <!--{"id":"btn_edit","title":"重置密码","iconCls":"icon-edit","action":"sso_user.resetPassword();", "type":0,"disabled":"false"}-->
                         
                       ]
       		/>
	 	</#if>
	</#if>
                       
</div>
<div data-options="region:'center',border:false">
 	<div id="subLayout" class="easyui-layout" data-options="fit:true,border:false">
        <div data-options="region:'north',border:false" >
            <div class="search-div">
			<form name="searchForm" id="searchForm" method="post" style="margin:0px">
				<table class="search-tb">
                     <tr>
                          <th style="width:67px">用户名：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="loginNameP" id="loginNameP"/></td>
                          
                          <th style="width:67px">真实姓名：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="sureNameP" id="sureNameP"/></td>
                          
                          <th style="width:67px">工号：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="employeeNumberP" id="employeeNumberP"/></td>
                          
                          <th style="width:67px">手机号：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="mobileP" id="mobileP"/></td>
                          
                          <th style="width:67px">身份证号：</th>
						  <td style="width:110px"><input class="easyui-validatebox" style="width:140px" name="idCardP" id="idCardP"/></td>
                          
                           <th style="width:67px">邮箱：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="emailP" id="emailP"/></td>
                          
				     </tr>
				     <tr>
				     	<th style="width:68px">绑定状态：</th>
                          <td style="width:110px">
                            <select id="bindStateP" name="bindStateP" class="easyui-combobox" style="width:80px;" data-options="editable:false">
                            	<option value="-1">全部</option>
								<option value="1">已绑定</option>
								<option value="0">未绑定</option>
							</select>
						  </td>
				     	<th style="width:67px">业务系统：</th>
                          <td>
                          	<select id="bizCodeP" name="bizCodeP" class="easyui-combobox" style="width:100px;" data-options="editable:false">
                            	<option value="-1">全部</option>
								<#if bizConfigList?exists>
								<#list bizConfigList as bizConf>
							    	<option value="${bizConf.bizCode}">${bizConf.bizCode}</option>
							    </#list>
							    </#if>
							</select>
                          </td>
                          <th style="width:97px">业务系统帐号：</th>
                          <td style="width:110px"><input class="easyui-validatebox" style="width:100px" name="bizCodeN" id="bizCodeN"/></td>
                          
				     	<th style="width:67px">账号状态：</th>
                          <td>
                            <select id="stateP" name="stateP" class="easyui-combobox" style="width:80px;" data-options="editable:false">
                            	<option value="-1">全部</option>
                            	<option value="0">初始状态</option>
								<option value="1">正常</option>
								<option value="2">已锁定</option>
							</select>
						  </td>
						  <th style="width:67px">组织机构：</th>
						  <td colspan="3">
						  		<input width="100%" class="easyui-combotree ipt" name="org_cond"  id="org_cond"   value=""  data-options="valueField:'id',textField:'text'"/>
						  </td>
				     </tr>
				     <tr>
					 	<th style="width:68px">更新时间：</th>
						<td>
							<@p.datetime name="startTime" id="startTime" labelWidth="145px" dateFmt="yyyy-MM-dd" labelText="" width="100px" height="22px" style="clear:both;float:left;" 
							required="false"  after="#afterStartTime" before="" afterCurrent="false"  beforeCurrnt="false" />
						</td>
					
					 	<th style="width:67px">至</th>
					 	<td>
							<@p.datetime name="endTime" id="endTime" labelWidth="145px" dateFmt="yyyy-MM-dd" labelText="" width="100px" height="22px" style="float:left;" 
							required="false"  after="#startTime" before="#beforeEndTime" afterCurrent="true"  beforeCurrnt="false" />
						</td>
					 	</td>
					 </tr>
				</table>
			</form>
			</div>
	   </div>
 	<div data-options="region:'center'">
 		<@p.datagrid id="sso_user_list" name="" title="一账通用户列表" loadUrl="/sso_user/ssoUserlist.json?sort=id&order=desc" saveUrl="" defaultColumn="" 
 			isHasToolBar="false" divToolbar="" width="" onClickRowEdit="true" singleSelect="false" pageSize='50'  checkOnSelect="true" selectOnCheck="true" 
			pagination="true" rownumbers="true" reduceHeight=54
 			columnsJsonList="[
 				 {field:'ck',checkbox:true},
 				 {field:'id',title:'ID', width:50, hidden:true},
				 {field:'loginName',title:'用户名', width:150},
				 {field:'sureName',title:'真实姓名', width:80},
				 {field:'idCard',title:'身份证号', width:150},
				 {field:'positionName',title:'岗位', width:80},
				 {field:'sex',title:'性别', width:40,
						formatter: function(value,row,index){
	                			if (value == 0) {
	                				return '女 ';
	                			}else if (value == 1) {
	                				return '男';
	                			}
	    						return '';
	    					}
				  },
				 {field:'mobile',title:'手机', width:100
				 },
				 {field:'email',title:'邮箱', width:130
				 },
				 {field:'employeeNumber',title:'工号', width:130
				 },
				 {field:'bizUser',title:'已绑定系统', width:200,
                	formatter: function(value,row,index){
            			var biz = $.parseJSON(value);
            			var bizStr = '';
            			var titleStr = '';
                		for(var item in biz){
                			bizStr += item + ':' + biz[item] + ',';
                			titleStr += item + ':' + biz[item] + '&#13;';
                		}
                		var rtnStr = bizStr ? bizStr.substring(0,bizStr.length-1):'';
						titleStr = titleStr ? titleStr.substring(0,titleStr.length-5):'';
                		return '<span title=\"' + titleStr + '\">' + rtnStr + '</span>';
    				}
 				 },
				 {field:'state',title:'状态', width:80,
                	formatter: function(value,row,index){
	                			if (value == '1') {
	                				return '正常 ';
	                			}else if (value == '2') {
	                				return '已锁定';
	                			}else if (value == '0') {
	                				return '初始状态';
	                			}
	    						return '';
	    					}
 				 },
 				 {field:'organizationCode',title:'组织机构', width:150, hidden:true},
				 {field:'organizationalUnitName',title:'组织机构', width:150},
				 {field:'createUser',title:'创建人', width:150},
				 {field:'updateTime',title:'更新时间', width:150,
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

<div id="dlg" class="easyui-dialog" style="width:500px;height:450px;" closed="true" buttons="#dlg-buttons2"
	data-options="
		title: '添加管理员',
		onClose: function() {
			$('#fm').form('disableValidation');
		}
	"
>
		<form id="fm" method="post" novalidate>
			<div class="fitem">
			</div>	
			
			<div class="fitem">
				<label>用户名:</label>
				<input id="loginName" class="easyui-validatebox ipt" name="loginName" validType="loginNameLength" style="width:200px;" required="true"/>
				<input id="id" name="id" type="hidden" class="easyui-validatebox ipt" />
			</div>	
			<div class="fitem">
				<label>真实姓名:</label>
				<input id="sureName" class="easyui-validatebox ipt" name="sureName" validType="loginNameLength" style="width:200px;" required="true"/>
			</div>
			<div class="fitem">
				<label>身份证号:</label>
				<input id="idCard" class="easyui-validatebox ipt" name="idCard" validType="idCardLength" style="width:200px;" required="true"/>
			</div>
			<div class="fitem">
				<label>岗位:</label>
				<input id="positionName" class="easyui-validatebox ipt" validType="positionName"  name="positionName" style="width:200px;"/>
			</div>
			<div class="fitem">
				<label>性别:</label>
				<select id="sex"  name="sex" style="width:200px;" class ="easyui-combobox" data-options="editable:false">
					<option value="1" selected ="selected">男</option>
					<option value="0">女</option>
				</select>
			</div>
			
			<div class="fitem" id="passwordArea">
				<label>密码:</label>
				<input class="easyui-validatebox ipt" type="text" id="password" name="password" style="width:104px;" required="true"/>
				<p id="p1" style="display:none">P1: I am paragraph 1</p>
				<a href="#" class="easyui-linkbutton" onclick="sso_user.getPassword()">生成随机密码</a>
			</div>
			<div class="fitem">
				<label>手机:</label>
				<input id="mobile" class="easyui-validatebox ipt" validType="phoneNum"  name="mobile" style="width:200px;"/>
			</div>
			
			<div class="fitem">
				<label>邮箱:</label>
				<input id="email" class="easyui-validatebox ipt" validType='email'  name="email" style="width:200px;"/>
			</div>
			
			<div class="fitem">
				<label>工号:</label>
				<input id="employeeNumber" class="easyui-validatebox ipt" name="employeeNumber" style="width:200px;"/>
			</div>
			
			<div class="fitem">
				<label>状态:</label>
				<select id="state"  name="state" style="width:200px;" class ="easyui-combobox" data-options="editable:false">
					<option value="0" >初始状态</option>
					<option value="1" >正常</option>
					<option value="2">锁定</option>
				</select>
			</div>
			<div class="fitem">
				<label>所属机构:</label>
				<!-- 机构树 -->
		        <div class="easyui-tree" data-options="region:'west',split:true, border:true" title="组织树（点此处展开）" style="width:260px; height:100%; border:0px solid #000000;">
				<ul id="org-unit-tree" style="width:260px;height:200px;" >
				</ul>
			</div>
			
		</form>
	<div id="dlg-buttons2">
		<a id="copyUser" href="javascript:void(0)" class="easyui-linkbutton" onclick="copyToClipboard('#p1')">复制账号密码</a>&nbsp;&nbsp;
		<a href="javascript:void(0)" class="easyui-linkbutton" id="save" name="save">保存</a>&nbsp;&nbsp;
		<a href="javascript:void(0)" class="easyui-linkbutton"  onclick="javascript:$('#dlg').dialog('close')">取消</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
</div>



<!-- 弹出绑定关系列表 -->  
<div id="dlgAccount" class="easyui-dialog" style="width: 750px; height:450px; padding: 10px 20px"  
		 title="绑定业务系统账户" closed="true" buttons="#dlg-buttons">  
		<div class="easyui-layout" data-options="fit:true,border:false">
	        <div data-options="region:'north',border:false" >
	            <div class="search-div">
				<form name="searchForm_bind" id="searchForm_bind" method="post" style="margin:0px">
					<table class="search-tb">
	                     <tr>
	                          <td>业务系统：</td><td><select id="bizId" class="easyui-combobox" style="width:100px;" data-options="editable:false"></td>
	                          <td>账号：</td><td><input id="bizLoginName" class="easyui-validatebox" style="width:100px" /></td>
	                          <td>密码：</td><td><input id="bizPwd" type="password" class="easyui-validatebox" style="width:100px"/></td>
	                          <td>
	                            <span class="easyui-linkbutton" plain="true" ></span>
								<a href="#" class="easyui-linkbutton" onclick="sso_user.bindUser()">绑定</a>
							  </td>
							  <td>
								<a href="#" class="easyui-linkbutton" onclick="sso_user.unbindUser()">解绑</a>
							  </td>
					      </tr>
					</table>
				</form>
				</div>
		   </div>
		   <div data-options="region:'center'">
		   <!-- 弹出的绑定关系列表 -->
	       <table id="datagrid" class="easyui-datagrid" style="width:500px;height:500px">  
	       </table> 
	       </div>
	     </div>
        
    </div>  
    <div id="dlg-buttons">  
        <a href="#" class="easyui-linkbutton" onclick="javascript:$('#dlgAccount').dialog('close')">关闭</a>
    </div>  
</div>

<!-- 弹出上传文件窗口 -->  
<div class="easyui-window" id="import-excel-template" title="文件上传" style="width:400px;height:160px;padding:2px;" closed="true">
    <form id="importFileForm" method="post" enctype="multipart/form-data" style="display:none">
        <table style="margin:5px;height:70px;">
            <tr>
                <td>请选择文件</td>
                <td width="5px;"></td>
                <td><input type="file" class="easyui-filebox" id="fileImport" name="fileImport" style="width:260px;"></td>
                <td></td></tr>
            <tr>
                <td colspan="4"><label id="fileName" /></td>
            </tr>
            <tr>
                <td colspan="4">
                    <label id="uploadInfo" />
                </td>
            </tr>
        </table><div style="text-align:center;clear:both;margin:5px;">
            <a id="uploadFile" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" onclick="sso_user.batchResetPwd()" href="javascript:void(0)">上传</a>
            <a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" onclick="sso_user.closeImportClick()" href="javascript:void(0)">关闭</a>
        </div>
    </form>
</div>

</body>
</html>