<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改密码</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   
<#include  "/WEB-INF/ftl/common/header.ftl" >
<script type="text/javascript">
	var loginName='${(Session["session_user"])!}';
	
	$(function(){
		$("#btn").click(
		function(){
			var flag = $("#fm").form('validate');
			if (flag == false) {
				return;
			}
			var param = {};
			param['newPwd']=$('#newPwd').val();
			if($('#newPwd').val()==''){
				alert('请输入新密码');
				return;
			}
			param['oldPwd']=$('#oldPwd').val();
			if($('#oldPwd').val()==''){
				alert('请输入旧密码');
				return;
			}
		    $.post(
		    	'${BasePath}/updatePwd',
		    	param,
		    	function(result){
		      		alert(result.msg);
		    	}
		    );
		}
		);
		
		$("#loginName").html(loginName);
	});
	
</script>
</head>
<body class="easyui-layout" data-options="fit:true,border:false">
<div data-options="region:'center',border:false">
 	<form id="fm" method="post" novalidate>
			<div class="fitem">
			<input id="id"  name="id" type="hidden"/>
			</div>	
			
			<div class="fitem">
				<label>登录名:</label><label id="loginName"></label>
			</div>	
			<div class="fitem">
				<label>旧密码:</label>
				<input type="password" id="oldPwd" class="easyui-validatebox ipt" style="width:200px;"/>
			</div>
			<div class="fitem">
				<label>新密码:</label>
				<input type="password" id="newPwd" class="easyui-validatebox ipt" style="width:200px;" validType="adminPwd"/>
			</div>
			<div class="fitem">
    			<input class="easyui-linkbutton" type="button" id="btn" value="提交" />
    		</div>
		</form>
</div>

</body>
</html>