<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#include  "/WEB-INF/ftl/common/header.ftl" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>SSO-Admin</title>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.min.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.easyui.min.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.menu.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.menubutton.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/jquery.dialog.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.datagrid.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/plugins/jquery.edatagrid.js"></script>
   <script type="text/javascript" src="${domainStatic}/resources/jquery-easyui-1.5.3/locale/easyui-lang-zh_CN.js"></script>
   <link rel="stylesheet" href="${domainStatic}/resources/jquery-easyui-1.5.3/themes/default/easyui.css" type="text/css"/>
   <link rel="stylesheet" href="${domainStatic}/resources/jquery-easyui-1.5.3/themes/icon.css" type="text/css"/>
   
</head>

    
<body >
	<div class="easyui-layout" data-options="fit:true">
	
    	<div id="content" region="center" title="" style="padding:5px;width:100%;height:100%">
    	
		    <div style="margin-bottom:10px" id="tb">
		    
			    <span>业务系统:</span>
				<select id="bizCode" class="easyui-combobox" name="bizCode" style="width:100px;">
				    <option value="0">OA</option>
				    <option value="1">HR</option>
				</select>
				<span>账号:</span>
				<input type="text" id="bizLoginName" name="bizLoginName" style="width:100px;"/>
				<span>密码:</span>
				<input type="password" id="bizPwd" name="bizPwd" style="width:100px;"/>
			
			    <span class="easyui-linkbutton" plain="true" ></span>
				<a href="#" class="easyui-linkbutton" onclick="bind()">绑定</a>
				<a href="#" class="easyui-linkbutton" onclick="unbind()">取消绑定</a>
			</div>
	    	<table id="list" style="width:100%;height:100%" >
				    <thead>
						<tr>
							<th >一账通账户</th>
							<th >业务系统</th>
							<th >业务系统账户</th>
							<th >操作</th>
						</tr>
				    </thead>
				    <tbody>
						<tr>
							<td>001</td><td>name1</td><td>2323</td>
						</tr>
						<tr>
							<td>002</td><td>name2</td><td>4612</td>
						</tr>
					</tbody>
			</table>
    	
		    
    	</div>
    	
    </div>

</body>

<script type="text/javascript">
	
	var loginName = '${loginName}';
	
	$(function(){ 
		
		
	}); 
	
	function bind(){
		alert(loginName);
	};
	
	function unbind(){
		alert(loginName);
	};
	
</script>


</html>
